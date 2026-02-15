package com.blog.writeapi.modules.comment.dtos;

import com.blog.writeapi.modules.post.dtos.PostDTO;
import com.blog.writeapi.modules.user.dtos.UserDTO;
import com.blog.writeapi.utils.enums.comment.CommentStatusEnum;

import java.time.OffsetDateTime;

public record CommentDTO(
        Long id,
        String content,
        CommentStatusEnum status,
        PostDTO post,
        UserDTO user,
        boolean edited,
        boolean pinned,
        String ipAddress,
        Long parentId,
        Long version,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
