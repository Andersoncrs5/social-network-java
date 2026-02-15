package com.blog.writeapi.modules.tag.dtos;

import com.blog.writeapi.utils.annotations.validations.global.StringClear.StringClear;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.global.slug.Slug;
import com.blog.writeapi.utils.annotations.validations.tag.existsTagById.ExistsTagById;
import com.blog.writeapi.utils.annotations.validations.tag.uniqueTagByName.UniqueTagByName;
import com.blog.writeapi.utils.annotations.validations.tag.uniqueTagBySlug.UniqueTagBySlug;
import jakarta.validation.constraints.Size;

public record UpdateTagDTO(

        @IsId
        @ExistsTagById
        Long id,

        @StringClear
        @Size(max = 70)
        @UniqueTagByName
        String name,

        @Size(max = 80)
        @Slug
        @UniqueTagBySlug
        String slug,

        @Size(max = 200)
        String description,

        Boolean isActive,
        Boolean isVisible,
        Boolean isSystem,
        java.time.OffsetDateTime lastUsedAt
) {
}
