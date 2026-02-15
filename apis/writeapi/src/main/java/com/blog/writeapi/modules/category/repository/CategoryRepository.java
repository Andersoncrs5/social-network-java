package com.blog.writeapi.modules.category.repository;

import com.blog.writeapi.modules.category.models.CategoryModel;
import com.blog.writeapi.utils.annotations.validations.global.slugConstraint.SlugConstraint;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<@NonNull CategoryModel, @NonNull Long> {
    Boolean existsByNameIgnoreCase(@NotNull String name);
    Optional<CategoryModel> findByNameIgnoreCase(@NotNull String name);
    Optional<CategoryModel> findBySlugIgnoreCase(@SlugConstraint String slug);
    Boolean existsBySlugIgnoreCase(@SlugConstraint String slug);
}
