package com.blog.writeapi.utils.res.swagger.comment;

import com.blog.writeapi.modules.comment.dtos.CommentDTO;
import com.blog.writeapi.utils.res.ResponseHttp;

public record ResponseCommentDTO(
        ResponseHttp<CommentDTO> dto
) {
}
