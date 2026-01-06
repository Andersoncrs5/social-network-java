package com.blog.writeapi.dtos.commentFavorite;

import com.blog.writeapi.dtos.comment.CommentDTO;
import com.blog.writeapi.dtos.user.UserDTO;

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
