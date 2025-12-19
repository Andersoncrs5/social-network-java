package com.blog.writeapi.dtos.tag;

import com.blog.writeapi.utils.annotations.valid.global.StringClear.StringClear;
import com.blog.writeapi.utils.annotations.valid.global.slug.Slug;
import com.blog.writeapi.utils.annotations.valid.tag.existsTagById.ExistsTagById;
import com.blog.writeapi.utils.annotations.valid.tag.existsTagByName.ExistsTagByName;
import com.blog.writeapi.utils.annotations.valid.tag.existsTagBySlug.ExistsTagBySlug;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateTagDTO(

        @NotNull
        @ExistsTagById
        Long id,

        @StringClear
        @Size(max = 70)
        @ExistsTagByName
        String name,

        @NotBlank
        @Size(max = 80)
        @Slug
        @ExistsTagBySlug
        String slug,

        @Size(max = 200)
        String description,

        Boolean isActive,
        Boolean isVisible,
        Boolean isSystem,
        java.time.OffsetDateTime lastUsedAt
) {
}
