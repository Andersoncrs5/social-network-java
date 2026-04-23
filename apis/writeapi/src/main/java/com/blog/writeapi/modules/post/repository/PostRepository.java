package com.blog.writeapi.modules.post.repository;

import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.global.slugConstraint.SlugConstraint;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<@NonNull PostModel, @NonNull Long> {
    Boolean existsBySlugIgnoreCase(@SlugConstraint String slug);

    Optional<PostModel> findBySlugIgnoreCase(@SlugConstraint String slug);

    @Modifying
    @Query("DELETE FROM PostModel s WHERE s.id = :id")
    int deleteAndCount(@Param("id") @IsId Long id);
}
