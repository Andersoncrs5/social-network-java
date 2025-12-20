package com.blog.writeapi.dtos.tag;

import java.time.OffsetDateTime;

public record TagDTO(
        Long id,
        String name,
        String slug,
        String description,
        Boolean isActive,
        Boolean isVisible,
        Boolean isSystem,
        Long postsCount,
        Long version,
        OffsetDateTime lastUsedAt,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
