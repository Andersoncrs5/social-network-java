package com.blog.writeapi.modules.commentFavorite.dtos;

import com.blog.writeapi.modules.comment.dtos.CommentDTO;
import com.blog.writeapi.modules.user.dtos.UserDTO;

import java.time.OffsetDateTime;

public record CommentFavoriteDTO(
        Long id,
        CommentDTO comment,
        UserDTO user,
        Long version,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
