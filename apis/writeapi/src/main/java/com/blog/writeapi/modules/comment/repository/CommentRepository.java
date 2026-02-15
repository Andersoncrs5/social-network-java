package com.blog.writeapi.modules.comment.repository;

import com.blog.writeapi.modules.comment.models.CommentModel;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<@NonNull CommentModel, @NonNull Long> {
}
