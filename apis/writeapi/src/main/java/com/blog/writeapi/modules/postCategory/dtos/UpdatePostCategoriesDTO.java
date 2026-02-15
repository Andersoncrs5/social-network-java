package com.blog.writeapi.modules.postCategory.dtos;

public record UpdatePostCategoriesDTO(
        Integer displayOrder,
        Boolean primary,
        Boolean active
) {
}
