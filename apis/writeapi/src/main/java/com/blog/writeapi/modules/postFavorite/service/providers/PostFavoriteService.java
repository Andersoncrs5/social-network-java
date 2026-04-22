package com.blog.writeapi.modules.postFavorite.service.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.postFavorite.gateway.PostFavoriteModuleGateway;
import com.blog.writeapi.modules.postFavorite.models.PostFavoriteModel;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.postFavorite.repository.PostFavoriteRepository;
import com.blog.writeapi.modules.postFavorite.service.docs.IPostFavoriteService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.classes.ResultToggle;
import com.blog.writeapi.utils.exceptions.BusinessRuleException;
import com.blog.writeapi.utils.exceptions.InternalServerErrorException;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.exceptions.UniqueConstraintViolationException;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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
    private final PostFavoriteModuleGateway gateway;

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
    @Retry(name = "delete-retry")
    public void delete(@IsModelInitialized PostFavoriteModel model) {
        this.repository.delete(model);
    }

    @Override
    @Retry(name = "create-retry")
    public PostFavoriteModel create(
            @IsModelInitialized PostModel post,
            @IsModelInitialized UserModel user
    ) {
        if (!user.getId().equals(post.getAuthor().getId())) {
            if (this.gateway.isBlocked(user.getId(), post.getAuthor().getId())) {
                throw new BusinessRuleException("You cannot favorite post from a blocked user.");
            }
        }

        PostFavoriteModel favor = new PostFavoriteModel().toBuilder()
                .id(generator.nextId())
                .post(post)
                .user(user)
                .build();

        try {
            return this.repository.save(favor);
        } catch (DataIntegrityViolationException e) {
            String message = Optional.of(e.getMostSpecificCause())
                    .map(Throwable::getMessage)
                    .orElse("").toLowerCase();

            if (message.contains("uk_posts_favorites")) {
                throw new UniqueConstraintViolationException("This post is already marked with favorite.");
            }

            throw new BusinessRuleException("Database integrity error: " + e.getMostSpecificCause().getMessage());
        } catch (Exception e) {
            throw new InternalServerErrorException("Error adding post to favorite");
        }
    }

    @Transactional
    public ResultToggle<PostFavoriteModel> toggle(
            @IsModelInitialized PostModel post,
            @IsModelInitialized UserModel user
    ) {
        Optional<PostFavoriteModel> favoriteOpt = this.repository.findByPostAndUser(post, user);

        if (favoriteOpt.isPresent()) {
            this.delete(favoriteOpt.get());
            return ResultToggle.removed();
        }

        return ResultToggle.added(this.create(post, user));
    }

}
