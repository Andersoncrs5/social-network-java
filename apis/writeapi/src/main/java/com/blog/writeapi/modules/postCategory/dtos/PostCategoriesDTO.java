package com.blog.writeapi.modules.postCategory.dtos;

import com.blog.writeapi.modules.category.dtos.CategoryDTO;
import com.blog.writeapi.modules.post.dtos.PostDTO;

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
