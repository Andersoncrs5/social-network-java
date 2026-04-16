package com.blog.writeapi.modules.commentFavorite.repository;

import com.blog.writeapi.modules.commentFavorite.models.CommentFavoriteModel;
import com.blog.writeapi.modules.comment.models.CommentModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentFavoriteRepository extends JpaRepository<@NonNull CommentFavoriteModel, @NonNull Long> {
    Optional<CommentFavoriteModel> findByCommentAndUser(@IsModelInitialized CommentModel comment, @IsModelInitialized UserModel user);
    @Query("""
           SELECT cf FROM CommentFavoriteModel cf
           JOIN FETCH cf.user
           JOIN FETCH cf.comment
           WHERE cf.user.id = :userId AND cf.comment.id = :commentId
           """)
    Optional<CommentFavoriteModel> findByUserIdAndCommentId(
            @Param("userId") Long userId,
            @Param("commentId") Long commentId
    );
}
