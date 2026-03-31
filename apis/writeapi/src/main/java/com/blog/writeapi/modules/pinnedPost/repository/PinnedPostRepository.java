package com.blog.writeapi.modules.pinnedPost.repository;

import com.blog.writeapi.modules.pinnedPost.model.PinnedPostModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PinnedPostRepository extends JpaRepository<PinnedPostModel, Long> {
    int countByUserId(@IsId Long userId);

    boolean existsByUserIdAndPostId(
            @IsId Long userId,
            @IsId Long postId
    );

    boolean existsByUserIdAndOrderIndex(
            @IsId Long userId,
            @NotNull int index
    );

    @Query("""
           SELECT p FROM PinnedPostModel p
           JOIN FETCH p.user
           JOIN FETCH p.post
           WHERE p.id = :id
           """)
    Optional<PinnedPostModel> findByIdWithRelationship(@Param("id") Long id);

}
