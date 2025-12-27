package com.blog.writeapi.dtos.postTag;

import com.blog.writeapi.dtos.post.PostDTO;
import com.blog.writeapi.dtos.tag.TagDTO;

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
