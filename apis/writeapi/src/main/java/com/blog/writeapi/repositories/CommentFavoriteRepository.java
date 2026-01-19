package com.blog.writeapi.repositories;

import com.blog.writeapi.models.CommentFavoriteModel;
import com.blog.writeapi.models.CommentModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentFavoriteRepository extends JpaRepository<@NonNull CommentFavoriteModel, @NonNull Long> {
    Optional<CommentFavoriteModel> findByCommentAndUser(@IsModelInitialized CommentModel comment, @IsModelInitialized UserModel user);
}
