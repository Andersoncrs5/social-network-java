package com.blog.writeapi.dtos.category;

import java.time.LocalDateTime;

public record CategoryDTO(
        Long id,
        String name,
        String description,
        String slug,
        Boolean isActive,
        Boolean visible,
        Integer displayOrder,
        Long version,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
