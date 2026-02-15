package com.blog.writeapi.modules.commentVote.repository;

import com.blog.writeapi.modules.comment.models.CommentModel;
import com.blog.writeapi.modules.commentVote.models.CommentVoteModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentVoteRepository extends JpaRepository<@NonNull CommentVoteModel, @NonNull Long> {
    Optional<CommentVoteModel> findByUserAndComment(@IsModelInitialized UserModel user, @IsModelInitialized CommentModel comment);
    Boolean existsByUserAndComment(@IsModelInitialized UserModel user, @IsModelInitialized CommentModel comment);
}
