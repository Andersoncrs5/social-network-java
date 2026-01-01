package com.blog.writeapi.dtos.comment;

import jakarta.validation.constraints.Size;

public record UpdateCommentDTO(
        @Size(max = 600)
        String content
) {
}
