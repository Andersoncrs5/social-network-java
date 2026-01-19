package com.blog.writeapi.repositories;

import com.blog.writeapi.models.CommentModel;
import com.blog.writeapi.models.CommentVoteModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentVoteRepository extends JpaRepository<@NonNull CommentVoteModel, @NonNull Long> {
    Optional<CommentVoteModel> findByUserAndComment(@IsModelInitialized UserModel user, @IsModelInitialized CommentModel comment);
    Boolean existsByUserAndComment(@IsModelInitialized UserModel user, @IsModelInitialized CommentModel comment);
}
