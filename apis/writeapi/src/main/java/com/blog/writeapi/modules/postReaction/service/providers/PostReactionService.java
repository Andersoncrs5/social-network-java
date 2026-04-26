package com.blog.writeapi.modules.postReaction.service.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.metric.dto.PostMetricEventDTO;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.postReaction.gateway.PostReactionModuleGateway;
import com.blog.writeapi.modules.postReaction.models.PostReactionModel;
import com.blog.writeapi.modules.reaction.models.ReactionModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.postReaction.repository.PostReactionRepository;
import com.blog.writeapi.modules.postReaction.service.docs.IPostReactionService;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.classes.ResultToggle;
import com.blog.writeapi.utils.enums.metric.ActionEnum;
import com.blog.writeapi.utils.enums.metric.PostMetricEnum;
import com.blog.writeapi.utils.exceptions.BusinessRuleException;
import com.blog.writeapi.utils.exceptions.InternalServerErrorException;
import com.blog.writeapi.utils.exceptions.UniqueConstraintViolationException;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class PostReactionService implements IPostReactionService {

    private final PostReactionRepository repository;
    private final Snowflake generator;
    private final PostReactionModuleGateway gateway;

    @Override
    @Retry(name = "create-retry")
    public PostReactionModel create(
            @IsModelInitialized PostModel post,
            @IsModelInitialized ReactionModel reaction,
            @IsModelInitialized UserModel user
    ) {
        if (!user.getId().equals(post.getAuthor().getId())) {
            if (this.gateway.isBlocked(user.getId(), post.getAuthor().getId())) {
                throw new BusinessRuleException("You cannot react to a post from a blocked user.");
            }
        }

        PostReactionModel postReaction = new PostReactionModel().toBuilder()
                .post(post)
                .reaction(reaction)
                .user(user)
                .id(this.generator.nextId())
                .build();

        try {
            PostReactionModel save = this.repository.save(postReaction);
            this.gateway.handleMetric(
                    PostMetricEventDTO.create(save.getPost().getId(), PostMetricEnum.REACTION, ActionEnum.SUM)
            );
            return save;
        } catch (DataIntegrityViolationException e) {
            String message = e.getMostSpecificCause().getMessage();

            if (message.contains("uk_post_user_reaction")) {
                throw new UniqueConstraintViolationException("You have already reacted on this post.");
            }

            throw new UniqueConstraintViolationException("You have already reacted on this post.");
        } catch (Exception e) {
            log.error("Error creating post vote: ", e);
            throw new InternalServerErrorException("Error processing your vote.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PostReactionModel> findByPostAndUser(
            @IsModelInitialized PostModel post,
            @IsModelInitialized UserModel user
    ) {
        return this.repository.findByPostAndUser(post, user);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean existsByPostAndUser(
            @IsModelInitialized PostModel post,
            @IsModelInitialized UserModel user
    ) {
        return this.repository.existsByPostAndUser(post, user);
    }

    @Override
    @Retry(name = "delete-retry")
    public void delete(@IsModelInitialized PostReactionModel model) {
        this.repository.delete(model);
        this.gateway.handleMetric(
                PostMetricEventDTO.create(model.getPost().getId(), PostMetricEnum.REACTION, ActionEnum.RED)
        );
    }

    @Override
    @Retry(name = "update-retry")
    public PostReactionModel updateSimple(@IsModelInitialized PostReactionModel model) {
        return repository.save(model);
    }

    public ResultToggle<PostReactionModel> toggle(
            @IsModelInitialized PostModel post,
            @IsModelInitialized ReactionModel reaction,
            @IsModelInitialized UserModel user
    ) {
        Optional<PostReactionModel> optional = this.repository.findByPostAndUser(post, user);

        if (optional.isPresent() && Objects.equals(optional.get().getReaction().getId(), reaction.getId())) {
            this.delete(optional.get());
            return ResultToggle.removed();
        }

        if (optional.isPresent()) {
            PostReactionModel model = optional.get();
            model.setReaction(reaction);
            return ResultToggle.updated(this.updateSimple(model));
        }

        return ResultToggle.added(this.create(post, reaction, user));
    }

}
