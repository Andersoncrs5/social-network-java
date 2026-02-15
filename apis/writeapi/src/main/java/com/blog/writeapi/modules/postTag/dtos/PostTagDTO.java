package com.blog.writeapi.modules.postTag.dtos;

import com.blog.writeapi.modules.post.dtos.PostDTO;
import com.blog.writeapi.modules.tag.dtos.TagDTO;

import java.time.OffsetDateTime;

public record PostTagDTO(
        Long id,
        PostDTO post,
        TagDTO tag,
        Boolean active,
        Boolean visible,
        Long version,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
