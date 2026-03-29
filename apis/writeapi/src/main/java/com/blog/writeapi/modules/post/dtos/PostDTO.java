package com.blog.writeapi.modules.post.dtos;

import com.blog.writeapi.modules.user.dtos.UserDTO;
import com.blog.writeapi.utils.enums.Post.PostStatusEnum;

import java.time.OffsetDateTime;

public record PostDTO(
    Long id,
    String title,
    String slug,
    String content,
    PostStatusEnum status,
    Integer readingTime,
    Double rankingScore,
    Boolean isFeatured,
    Long parentId,
    UserDTO author,
    Long version,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
}
