package com.blog.writeapi.services.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.models.CommentFavoriteModel;
import com.blog.writeapi.models.CommentModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.repositories.CommentFavoriteRepository;
import com.blog.writeapi.services.interfaces.ICommentFavoriteService;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentFavoriteService implements ICommentFavoriteService {

    private final CommentFavoriteRepository repository;
    private final Snowflake generator;

    @Override
    @Transactional(readOnly = true)
    public CommentFavoriteModel getById(@IsId Long id) {
        return this.repository.findById(id).orElseThrow(
                () -> new ModelNotFoundException("Comment marked with favorite was not found")
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CommentFavoriteModel getByCommentIdAndUserId(
            @IsModelInitialized CommentModel comment,
            @IsModelInitialized UserModel user
    ) {
        return this.repository.findByCommentAndUser(comment, user).orElseThrow(
                () -> new ModelNotFoundException("Comment marked with favorite was not found")
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CommentFavoriteModel> findByCommentIdAndUserId(
            @IsModelInitialized CommentModel comment,
            @IsModelInitialized UserModel user
    ) {
        return this.repository.findByCommentAndUser(comment, user);
    }


    @Override
    @Transactional
    @Retry(name = "create-retry")
    public CommentFavoriteModel add(
            @IsModelInitialized UserModel user,
            @IsModelInitialized CommentModel comment
    ) {
        CommentFavoriteModel favor = new CommentFavoriteModel().toBuilder()
                .id(generator.nextId())
                .user(user)
                .comment(comment)
                .build();

        return this.repository.save(favor);
    }

    @Override
    @Transactional
    @Retry(name = "delete-retry")
    public void remove(@IsModelInitialized CommentFavoriteModel model) {
        this.repository.delete(model);
    }

}
