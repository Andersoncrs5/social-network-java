package com.blog.writeapi.dtos.postCategories;

import com.blog.writeapi.dtos.category.CategoryDTO;
import com.blog.writeapi.dtos.post.PostDTO;

import java.time.OffsetDateTime;

public record PostCategoriesDTO(
        Long id,
        PostDTO post,
        CategoryDTO category,
        Integer displayOrder,
        boolean primary,
        boolean active,
        Long version,
        OffsetDateTime createdAt,
        OffsetDateTime updateAt
) {
}
