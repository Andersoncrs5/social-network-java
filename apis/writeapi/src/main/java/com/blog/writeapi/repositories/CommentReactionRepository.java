package com.blog.writeapi.repositories;

import com.blog.writeapi.models.CommentModel;
import com.blog.writeapi.models.CommentReactionModel;
import com.blog.writeapi.models.UserModel;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentReactionRepository extends JpaRepository<@NonNull CommentReactionModel, @NonNull Long> {
    Optional<CommentReactionModel> findByUserAndComment(UserModel user, CommentModel comment);
}
