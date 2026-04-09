package com.blog.writeapi.modules.postReadingList.dto;

import com.blog.writeapi.modules.post.dtos.PostDTO;
import com.blog.writeapi.modules.user.dtos.UserDTO;

import java.time.OffsetDateTime;

public record PostReadingDTO(
        Long id,
        Long version,
        UserDTO user,
        PostDTO post,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
