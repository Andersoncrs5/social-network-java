package com.blog.writeapi.services.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.dtos.postVote.TogglePostVoteDTO;
import com.blog.writeapi.models.PostModel;
import com.blog.writeapi.models.PostVoteModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.repositories.PostVoteRepository;
import com.blog.writeapi.services.interfaces.IPostVoteService;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    @Transactional
    @Retry(name = "create-retry")
    public PostVoteModel create(TogglePostVoteDTO dto, @IsModelInitialized UserModel user, @IsModelInitialized PostModel post) {
        PostVoteModel vote = new PostVoteModel().toBuilder()
                .id(this.generator.nextId())
                .type(dto.type())
                .post(post)
                .user(user)
                .build();

        return this.repository.save(vote);
    }

    @Override
    @Transactional
    @Retry(name = "update-retry")
    public PostVoteModel updateSimple(@IsModelInitialized PostVoteModel vote) {
        return this.repository.save(vote);
    }



}
