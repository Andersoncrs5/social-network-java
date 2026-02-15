package com.blog.writeapi.modules.commentVote.dtos;

import com.blog.writeapi.modules.comment.dtos.CommentDTO;
import com.blog.writeapi.modules.user.dtos.UserDTO;
import com.blog.writeapi.utils.enums.votes.VoteTypeEnum;

import java.time.OffsetDateTime;

public record CommentVoteDTO(
        Long id,
        CommentDTO comment,
        UserDTO user,
        VoteTypeEnum type,
        Long version,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
