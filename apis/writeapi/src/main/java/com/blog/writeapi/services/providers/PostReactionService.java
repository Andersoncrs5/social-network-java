package com.blog.writeapi.services.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.dtos.postReaction.CreatePostReactionDTO;
import com.blog.writeapi.models.PostModel;
import com.blog.writeapi.models.PostReactionModel;
import com.blog.writeapi.models.ReactionModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.repositories.PostReactionRepository;
import com.blog.writeapi.services.interfaces.IPostReactionService;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class PostReactionService implements IPostReactionService {

    private final PostReactionRepository repository;
    private final Snowflake generator;

    @Override
    @Transactional
    @Retry(name = "create-retry")
    public PostReactionModel create(PostModel post, ReactionModel reaction, UserModel user) {
        PostReactionModel postReaction = new PostReactionModel().toBuilder()
                .post(post)
                .reaction(reaction)
                .user(user)
                .id(this.generator.nextId())
                .build();

        return this.repository.save(postReaction);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PostReactionModel> findByPostAndUser(PostModel post, UserModel user) {
        return this.repository.findByPostAndUser(post, user);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean existsByPostAndUser(PostModel post, UserModel user) {
        return this.repository.existsByPostAndUser(post, user);
    }

    @Override
    @Transactional
    @Retry(name = "delete-retry")
    public void delete(PostReactionModel model) {
        this.repository.delete(model);
    }

    @Override
    @Transactional
    @Retry(name = "update-retry")
    public PostReactionModel updateSimple(PostReactionModel model) {
        return repository.save(model);
    }

}
