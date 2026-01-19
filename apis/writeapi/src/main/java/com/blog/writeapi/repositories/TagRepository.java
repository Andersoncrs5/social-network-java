package com.blog.writeapi.repositories;

import com.blog.writeapi.models.TagModel;
import com.blog.writeapi.utils.annotations.valid.global.slugConstraint.SlugConstraint;
import jakarta.validation.constraints.NotBlank;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<@NonNull TagModel, @NonNull Long> {
    Optional<TagModel> findByName(@NotBlank String name);
    Boolean existsByName(@NotBlank String name);

    Optional<TagModel> findBySlug(@SlugConstraint String slug);
    Boolean existsBySlug(@SlugConstraint String slug);
}
