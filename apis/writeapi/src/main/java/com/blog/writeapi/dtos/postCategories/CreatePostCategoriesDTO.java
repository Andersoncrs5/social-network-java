package com.blog.writeapi.dtos.postCategories;

public record CreatePostCategoriesDTO(
        Long postId,
        Long categoryId,
        Integer displayOrder,
        boolean primary,
        boolean active
) {
}
