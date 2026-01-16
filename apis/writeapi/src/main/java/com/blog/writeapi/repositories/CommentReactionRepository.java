package com.blog.writeapi.repositories;

import com.blog.writeapi.models.CommentReactionModel;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentReactionRepository extends JpaRepository<@NonNull CommentReactionModel, @NonNull Long> {
}
