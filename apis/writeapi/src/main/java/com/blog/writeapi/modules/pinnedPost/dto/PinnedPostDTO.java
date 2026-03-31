package com.blog.writeapi.modules.pinnedPost.dto;

import com.blog.writeapi.modules.post.dtos.PostDTO;
import com.blog.writeapi.modules.user.dtos.UserDTO;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;

import java.time.OffsetDateTime;

public record PinnedPostDTO(
        @IsId
        Long id,
        UserDTO user,
        PostDTO post,
        int orderIndex,
        Long version,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
