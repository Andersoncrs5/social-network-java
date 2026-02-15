package com.blog.writeapi.modules.category.dtos;

import com.blog.writeapi.utils.annotations.validations.category.noCircularCategoryParent.NoCircularCategoryParent;
import com.blog.writeapi.utils.annotations.validations.category.uniqueName.UniqueCategoryName;
import com.blog.writeapi.utils.annotations.validations.category.uniqueSlug.UniqueCategorySlug;
import com.blog.writeapi.utils.annotations.validations.global.StringClear.StringClear;
import com.blog.writeapi.utils.annotations.validations.global.forbidden.ForbiddenMatches;
import com.blog.writeapi.utils.annotations.validations.global.slug.Slug;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@NoCircularCategoryParent
public record UpdateCategoryDTO(

        @PositiveOrZero(message = "The ID provided must be a positive number.")
        Long id,

        @StringClear
        @Size(min = 2, max = 100)
        @ForbiddenMatches({"admin", "root", "guest"})
        @UniqueCategoryName
        String name,

        @Size(max = 500)
        String description,

        @Slug
        @Size(max = 160)
        @UniqueCategorySlug
        String slug,

        Boolean isActive,
        Boolean visible,

        Integer displayOrder,

        Long parentId,

        Boolean isRoot
) {
}
