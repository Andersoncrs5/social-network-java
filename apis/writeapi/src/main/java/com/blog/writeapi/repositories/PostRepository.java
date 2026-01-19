package com.blog.writeapi.repositories;

import com.blog.writeapi.models.PostModel;
import com.blog.writeapi.utils.annotations.valid.global.slugConstraint.SlugConstraint;
import jakarta.validation.constraints.NotBlank;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<@NonNull PostModel, @NonNull Long> {
    Boolean existsBySlug(@SlugConstraint String slug);

    Optional<PostModel> findBySlug(@SlugConstraint String slug);
}
