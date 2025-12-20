package com.blog.writeapi.dtos.post;

import com.blog.writeapi.dtos.user.UserDTO;
import com.blog.writeapi.models.enums.Post.PostStatusEnum;

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
    UserDTO author,
    Long version,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
}
