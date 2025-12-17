package com.blog.writeapi.dtos.category;

import com.blog.writeapi.utils.annotations.valid.category.uniqueName.UniqueCategoryName;
import com.blog.writeapi.utils.annotations.valid.category.uniqueSlug.UniqueCategorySlug;
import com.blog.writeapi.utils.annotations.valid.global.StringClear.StringClear;
import com.blog.writeapi.utils.annotations.valid.global.forbidden.ForbiddenMatches;
import com.blog.writeapi.utils.annotations.valid.global.slug.Slug;
import jakarta.validation.constraints.*;

public record CreateCategoryDTO(

        @NotBlank
        @StringClear
        @UniqueCategoryName
        @Size(min = 2, max = 100)
        @ForbiddenMatches({"admin", "root", "guest"})
        String name,

        @Size(max = 500)
        String description,

        @Slug
        @NotBlank
        @Size(max = 160)
        @UniqueCategorySlug
        String slug,

        @NotNull
        Boolean isActive,

        @NotNull
        Boolean visible,

        @NotNull
        @PositiveOrZero(message = "Display order must be zero or a positive number.")
        Integer displayOrder,

        Long parentId
) {
}
