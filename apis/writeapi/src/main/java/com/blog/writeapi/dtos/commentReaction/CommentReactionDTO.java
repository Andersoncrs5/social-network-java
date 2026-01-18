package com.blog.writeapi.dtos.commentReaction;

import com.blog.writeapi.dtos.comment.CommentDTO;
import com.blog.writeapi.dtos.post.PostDTO;
import com.blog.writeapi.dtos.reaction.ReactionDTO;
import com.blog.writeapi.dtos.user.UserDTO;

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
