package com.blog.writeapi.utils.res.swagger.commentReaction;

import com.blog.writeapi.dtos.commentReaction.CommentReactionDTO;
import com.blog.writeapi.utils.res.ResponseHttp;

public record ResponseCommentReactionDTO(
        ResponseHttp<CommentReactionDTO> dto
) {
}
