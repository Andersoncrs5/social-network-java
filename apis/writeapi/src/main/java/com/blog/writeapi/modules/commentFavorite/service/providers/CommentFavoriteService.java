package com.blog.writeapi.modules.commentFavorite.service.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.commentFavorite.gateway.CommentFavoriteModuleGateway;
import com.blog.writeapi.modules.commentFavorite.models.CommentFavoriteModel;
import com.blog.writeapi.modules.comment.models.CommentModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.commentFavorite.repository.CommentFavoriteRepository;
import com.blog.writeapi.modules.commentFavorite.service.docs.ICommentFavoriteService;
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

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentFavoriteService implements ICommentFavoriteService {

    private final CommentFavoriteRepository repository;
    private final Snowflake generator;
    private final CommentFavoriteModuleGateway gateway;

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
    @Retry(name = "create-retry")
    public CommentFavoriteModel add(
            @IsModelInitialized UserModel user,
            @IsModelInitialized CommentModel comment
    ) {
        if (!user.getId().equals(comment.getAuthor().getId())) {
            if (this.gateway.isBlocked(user.getId(), comment.getAuthor().getId())) {
                throw new BusinessRuleException("You cannot favorite a comment from a blocked user.");
            }
        }

        CommentFavoriteModel favor = new CommentFavoriteModel().toBuilder()
                .id(generator.nextId())
                .user(user)
                .comment(comment)
                .build();

        try {
            return this.repository.save(favor);
        } catch (DataIntegrityViolationException e) {
            String message = Optional.of(e.getMostSpecificCause())
                    .map(Throwable::getMessage)
                    .orElse("").toLowerCase();

            if (message.contains("uk_comments_favorites")) {
                throw new UniqueConstraintViolationException("This comment is already in your favorites.");
            }

            throw new BusinessRuleException("Database integrity error: " + message);
        } catch (Exception e) {
            log.error("Error adding comment to favorites: ", e);
            throw new InternalServerErrorException("Error processing your request.");
        }
    }

    @Override
    @Retry(name = "delete-retry")
    public void remove(@IsModelInitialized CommentFavoriteModel model) {
        this.repository.delete(model);
    }

    @Override
    @Retry(name = "toggle-retry")
    @Transactional
    public ResultToggle<CommentFavoriteModel> toggle(
            @IsModelInitialized UserModel user,
            @IsModelInitialized CommentModel comment
    ) {
        Optional<CommentFavoriteModel> favorite = this.repository.findByCommentAndUser(comment, user);

        if (favorite.isPresent()) {
            this.remove(favorite.get());
            return ResultToggle.removed();
        }

        return ResultToggle.added(this.add(user, comment));
    }
}
