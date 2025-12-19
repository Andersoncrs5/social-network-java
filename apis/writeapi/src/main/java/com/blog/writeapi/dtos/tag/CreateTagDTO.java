package com.blog.writeapi.dtos.tag;

import java.time.OffsetDateTime;

public record CreateTagDTO(
        String name,
        String slug,
        String description,
        Boolean isActive,
        Boolean isVisible,
        Boolean isSystem,
        OffsetDateTime lastUsedAt
) {
}
