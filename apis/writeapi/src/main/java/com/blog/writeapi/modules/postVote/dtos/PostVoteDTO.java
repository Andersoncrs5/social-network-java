package com.blog.writeapi.modules.postVote.dtos;

import com.blog.writeapi.modules.post.dtos.PostDTO;
import com.blog.writeapi.modules.user.dtos.UserDTO;
import com.blog.writeapi.utils.enums.votes.VoteTypeEnum;

import java.time.OffsetDateTime;

public record PostVoteDTO(
        Long id,
        PostDTO post,
        UserDTO user,
        VoteTypeEnum type,
        Long version,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
