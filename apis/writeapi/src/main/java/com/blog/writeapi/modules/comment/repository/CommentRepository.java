package com.blog.writeapi.modules.comment.repository;

import com.blog.writeapi.modules.comment.models.CommentModel;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<@NonNull CommentModel, @NonNull Long> {
    @Query("SELECT c FROM CommentModel c JOIN FETCH c.author WHERE c.id = :id")
    Optional<CommentModel> findByIdWithAuthor(@Param("id") Long id);
}
