package com.blog.writeapi.modules.comment.dtos;

import jakarta.validation.constraints.Size;

public record UpdateCommentDTO(
        @Size(max = 600)
        String content
) {
}
