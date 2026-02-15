package com.blog.writeapi.modules.category.dtos;

import com.blog.writeapi.utils.annotations.validations.category.uniqueName.UniqueCategoryName;
import com.blog.writeapi.utils.annotations.validations.category.uniqueSlug.UniqueCategorySlug;
import com.blog.writeapi.utils.annotations.validations.global.StringClear.StringClear;
import com.blog.writeapi.utils.annotations.validations.global.forbidden.ForbiddenMatches;
import com.blog.writeapi.utils.annotations.validations.global.slug.Slug;
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
