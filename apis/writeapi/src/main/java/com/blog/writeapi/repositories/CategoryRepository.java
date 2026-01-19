package com.blog.writeapi.repositories;

import com.blog.writeapi.models.CategoryModel;
import com.blog.writeapi.utils.annotations.valid.global.slugConstraint.SlugConstraint;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<@NonNull CategoryModel, @NonNull Long> {
    Boolean existsByNameIgnoreCase(@NotNull String name);
    Optional<CategoryModel> findByNameIgnoreCase(@NotNull String name);
    Optional<CategoryModel> findBySlug(@SlugConstraint String slug);
    Boolean existsBySlug(@SlugConstraint String slug);
}
