package com.blog.writeapi.dtos.category;

import com.blog.writeapi.utils.annotations.valid.category.existsByCategoryId.ExistsByCategoryId;
import com.blog.writeapi.utils.annotations.valid.category.noCircularCategoryParent.NoCircularCategoryParent;
import com.blog.writeapi.utils.annotations.valid.category.uniqueName.UniqueCategoryName;
import com.blog.writeapi.utils.annotations.valid.category.uniqueSlug.UniqueCategorySlug;
import com.blog.writeapi.utils.annotations.valid.global.StringClear.StringClear;
import com.blog.writeapi.utils.annotations.valid.global.forbidden.ForbiddenMatches;
import com.blog.writeapi.utils.annotations.valid.global.slug.Slug;
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
