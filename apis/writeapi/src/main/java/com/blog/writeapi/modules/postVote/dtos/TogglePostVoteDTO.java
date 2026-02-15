package com.blog.writeapi.modules.postVote.dtos;

import com.blog.writeapi.utils.enums.votes.VoteTypeEnum;

public record TogglePostVoteDTO(
        Long postID,
        VoteTypeEnum type
) {
}
