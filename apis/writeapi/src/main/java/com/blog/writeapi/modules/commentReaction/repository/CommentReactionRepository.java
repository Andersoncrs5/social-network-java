package com.blog.writeapi.modules.commentReaction.repository;

import com.blog.writeapi.modules.comment.models.CommentModel;
import com.blog.writeapi.modules.commentReaction.models.CommentReactionModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentReactionRepository extends JpaRepository<@NonNull CommentReactionModel, @NonNull Long> {
    Optional<CommentReactionModel> findByUserAndComment(@IsModelInitialized UserModel user, @IsModelInitialized CommentModel comment);
}
