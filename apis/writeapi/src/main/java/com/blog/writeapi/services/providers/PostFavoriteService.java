package com.blog.writeapi.services.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.models.PostFavoriteModel;
import com.blog.writeapi.models.PostModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.repositories.PostFavoriteRepository;
import com.blog.writeapi.services.interfaces.IPostFavoriteService;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
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
public class PostFavoriteService implements IPostFavoriteService {

    private final PostFavoriteRepository repository;
    private final Snowflake generator;

    @Override
    @Transactional(readOnly = true)
    public PostFavoriteModel getByIdSimple(@IsId Long id) {
        return this.repository.findById(id).orElseThrow(
                () -> new ModelNotFoundException("PostFavorite not found")
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean existsByPostAndUser(@IsModelInitialized PostModel post, @IsModelInitialized UserModel user) {
        return this.repository.existsByPostAndUser(post, user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PostFavoriteModel> getByPostAndUser(@IsModelInitialized PostModel post, @IsModelInitialized UserModel user) {
        return this.repository.findByPostAndUser(post, user);
    }

    @Override
    @Transactional
    @Retry(name = "delete-retry")
    public void delete(@IsModelInitialized PostFavoriteModel model) {
        this.repository.delete(model);
    }

    @Override
    @Transactional
    @Retry(name = "create-retry")
    public PostFavoriteModel create(@IsModelInitialized PostModel post, @IsModelInitialized UserModel user) {
        PostFavoriteModel favor = new PostFavoriteModel().toBuilder()
                .id(generator.nextId())
                .post(post)
                .user(user)
                .build();

        return this.repository.save(favor);
    }

}
