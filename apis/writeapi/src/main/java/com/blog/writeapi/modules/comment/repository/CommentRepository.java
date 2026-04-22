package com.blog.writeapi.modules.comment.repository;

import com.blog.writeapi.modules.comment.models.CommentModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<@NonNull CommentModel, @NonNull Long> {
    @Query("SELECT c FROM CommentModel c JOIN FETCH c.author WHERE c.id = :id")
    Optional<CommentModel> findByIdWithAuthor(@Param("id") Long id);

    @Modifying
    @Query("DELETE FROM CommentModel e WHERE e.id = :id")
    int deleteByID(@Param("id") Long id);
}
