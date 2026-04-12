package com.blog.writeapi.modules.postShare.dto;

import com.blog.writeapi.modules.post.dtos.PostDTO;
import com.blog.writeapi.modules.user.dtos.UserDTO;
import com.blog.writeapi.utils.enums.postShare.SharePlatformEnum;

import java.time.OffsetDateTime;

public record PostShareDTO(
        Long id,
        PostDTO post,
        UserDTO user,
        SharePlatformEnum platform,
        String shareUrl,
        Long version,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
