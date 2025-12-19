package com.blog.writeapi.dtos.tag;

import com.blog.writeapi.utils.annotations.valid.global.StringClear.StringClear;
import com.blog.writeapi.utils.annotations.valid.tag.existsTagById.ExistsTagById;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public record UpdateTagDTO(
        @NotNull
        @ExistsTagById
        Long id,

        @StringClear
        @Size(max = 70)
        String name,

        @NotBlank
        @Size(max = 80)
        String slug,

        @Size(max = 200)
        String description,

        Boolean isActive,
        Boolean isVisible,
        Boolean isSystem,
        OffsetDateTime lastUsedAt
) {
}
