package com.blog.writeapi.dtos.postVote;

import com.blog.writeapi.dtos.post.PostDTO;
import com.blog.writeapi.dtos.user.UserDTO;
import com.blog.writeapi.models.enums.votes.VoteTypeEnum;

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
