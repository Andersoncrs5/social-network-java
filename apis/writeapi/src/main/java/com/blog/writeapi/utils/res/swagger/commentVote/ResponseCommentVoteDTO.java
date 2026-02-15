package com.blog.writeapi.utils.res.swagger.commentVote;

import com.blog.writeapi.modules.commentVote.dtos.CommentVoteDTO;
import com.blog.writeapi.utils.res.ResponseHttp;

public record ResponseCommentVoteDTO(
        ResponseHttp<CommentVoteDTO> dto
) {
}
