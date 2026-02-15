package com.blog.writeapi.modules.comment.dtos;

import com.blog.writeapi.utils.annotations.validations.comment.existsCommentById.ExistsCommentById;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.post.existsPostById.ExistsPostById;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCommentDTO(

        @NotBlank
        @Size(min = 1, max = 600)
        String content,

        @IsId
        @ExistsPostById
        Long postID,

        @ExistsCommentById
        Long parentId
) {
}
