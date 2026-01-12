package com.blog.writeapi.dtos.postVote;

import com.blog.writeapi.models.enums.votes.VoteTypeEnum;

public record TogglePostVoteDTO(
        Long postID,
        VoteTypeEnum type
) {
}
