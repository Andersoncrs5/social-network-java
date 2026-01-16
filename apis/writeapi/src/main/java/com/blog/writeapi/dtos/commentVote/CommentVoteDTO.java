package com.blog.writeapi.dtos.commentVote;

import com.blog.writeapi.dtos.comment.CommentDTO;
import com.blog.writeapi.dtos.user.UserDTO;
import com.blog.writeapi.models.enums.votes.VoteTypeEnum;

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
