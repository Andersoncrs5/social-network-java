package com.blog.writeapi.modules.commentFavorite.repository;

import com.blog.writeapi.modules.commentFavorite.models.CommentFavoriteModel;
import com.blog.writeapi.modules.comment.models.CommentModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentFavoriteRepository extends JpaRepository<@NonNull CommentFavoriteModel, @NonNull Long> {
    Optional<CommentFavoriteModel> findByCommentAndUser(@IsModelInitialized CommentModel comment, @IsModelInitialized UserModel user);
}
