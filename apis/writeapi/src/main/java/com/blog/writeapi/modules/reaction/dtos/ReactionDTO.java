package com.blog.writeapi.modules.reaction.dtos;

import com.blog.writeapi.utils.enums.reaction.ReactionTypeEnum;

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
