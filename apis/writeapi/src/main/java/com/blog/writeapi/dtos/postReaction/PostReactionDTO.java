package com.blog.writeapi.dtos.postReaction;

import com.blog.writeapi.dtos.post.PostDTO;
import com.blog.writeapi.dtos.reaction.ReactionDTO;
import com.blog.writeapi.dtos.user.UserDTO;

import java.time.OffsetDateTime;

public record PostReactionDTO(
        Long id,
        UserDTO user,
        PostDTO post,
        ReactionDTO reaction,
        Long version,
        OffsetDateTime createdAt,
        OffsetDateTime updateAt
) {
}
