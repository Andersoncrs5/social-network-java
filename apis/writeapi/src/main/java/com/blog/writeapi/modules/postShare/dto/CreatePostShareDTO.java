package com.blog.writeapi.modules.postShare.dto;

import com.blog.writeapi.utils.enums.postShare.SharePlatformEnum;

public record CreatePostShareDTO(
        Long postId,
        SharePlatformEnum platform
) {
}
