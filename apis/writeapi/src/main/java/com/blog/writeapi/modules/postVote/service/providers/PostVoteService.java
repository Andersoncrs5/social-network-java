package com.blog.writeapi.modules.postVote.service.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.postVote.dtos.TogglePostVoteDTO;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.postVote.gateway.PostVoteGatewayModule;
import com.blog.writeapi.modules.postVote.models.PostVoteModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.postVote.repository.PostVoteRepository;
import com.blog.writeapi.modules.postVote.service.docs.IPostVoteService;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.classes.ResultToggle;
import com.blog.writeapi.utils.exceptions.BusinessRuleException;
import com.blog.writeapi.utils.exceptions.InternalServerErrorException;
import com.blog.writeapi.utils.exceptions.UniqueConstraintViolationException;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class PostVoteService implements IPostVoteService {

    private final PostVoteRepository repository;
    private final Snowflake generator;
    private final PostVoteGatewayModule gateway;

    @Override
    @Transactional(readOnly = true)
    public Optional<PostVoteModel> findByUserAndPost(@IsModelInitialized UserModel user, @IsModelInitialized PostModel post) {
        return this.repository.findByUserAndPost(user, post);
    }

    @Override
    @Transactional
    @Retry(name = "delete-retry")
    public void delete(@IsModelInitialized PostVoteModel vote) {
        this.repository.delete(vote);
    }

    @Override
    @Retry(name = "create-retry")
    public PostVoteModel create(TogglePostVoteDTO dto, @IsModelInitialized UserModel user, @IsModelInitialized PostModel post) {

        if (!user.getId().equals(post.getAuthor().getId())) {
            if (this.gateway.isBlocked(user.getId(), post.getAuthor().getId())) {
                throw new BusinessRuleException("You cannot react to a story from a blocked user.");
            }
        }

        PostVoteModel vote = new PostVoteModel().toBuilder()
                .id(this.generator.nextId())
                .type(dto.type())
                .post(post)
                .user(user)
                .build();

        try {
            return this.repository.save(vote);
        } catch (DataIntegrityViolationException e) {
            String message = e.getMostSpecificCause().getMessage();

            if (message.contains("uk_post_vote")) {
                throw new UniqueConstraintViolationException("You have already voted on this post.");
            }

            throw new UniqueConstraintViolationException("You have already voted on this post.");
        } catch (Exception e) {
            log.error("Error creating post vote: ", e);
            throw new InternalServerErrorException("Error processing your vote.");
        }
    }

    @Override
    @Transactional
    @Retry(name = "update-retry")
    public PostVoteModel updateSimple(@IsModelInitialized PostVoteModel vote) {
        return this.repository.save(vote);
    }

    @Override @Transactional
    @Retry(name = "toggle-retry")
    public ResultToggle<PostVoteModel> toggle(
            @IsModelInitialized UserModel user,
            @IsModelInitialized PostModel post,
            @NotNull TogglePostVoteDTO dto
    ) {
        Optional<PostVoteModel> voteOpt = this.repository.findByUserAndPost(user, post);

        if (voteOpt.isPresent()) {
            PostVoteModel vote = voteOpt.get();

            if (vote.getType().equals(dto.type())) {
                this.delete(vote);
                return ResultToggle.removed();
            }

            vote.setType(dto.type());
            return ResultToggle.updated(this.updateSimple(vote));
        }

        return ResultToggle.added(this.create(dto, user, post));
    }

}
