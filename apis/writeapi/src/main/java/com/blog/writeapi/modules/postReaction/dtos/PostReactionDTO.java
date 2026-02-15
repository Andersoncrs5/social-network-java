package com.blog.writeapi.modules.postReaction.dtos;

import com.blog.writeapi.modules.post.dtos.PostDTO;
import com.blog.writeapi.modules.reaction.dtos.ReactionDTO;
import com.blog.writeapi.modules.user.dtos.UserDTO;

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
