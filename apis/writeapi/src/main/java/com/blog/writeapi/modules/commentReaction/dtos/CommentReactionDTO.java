package com.blog.writeapi.modules.commentReaction.dtos;

import com.blog.writeapi.modules.comment.dtos.CommentDTO;
import com.blog.writeapi.modules.reaction.dtos.ReactionDTO;
import com.blog.writeapi.modules.user.dtos.UserDTO;

import java.time.OffsetDateTime;

public record CommentReactionDTO(
        Long id,
        UserDTO user,
        CommentDTO comment,
        ReactionDTO reaction,
        Long version,
        OffsetDateTime createdAt,
        OffsetDateTime updateAt
) {
}
