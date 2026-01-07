package com.blog.writeapi.dtos.reaction;

import com.blog.writeapi.models.enums.reaction.ReactionTypeEnum;

import java.time.OffsetDateTime;

public record ReactionDTO(
        Long id,
        String name,
        String emojiUrl,
        String emojiUnicode,
        Long displayOrder,
        ReactionTypeEnum type,
        Boolean active,
        Boolean visible,
        Long version,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
