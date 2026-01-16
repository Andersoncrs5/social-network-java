package com.blog.writeapi.dtos.commentVote;

import com.blog.writeapi.models.enums.votes.VoteTypeEnum;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;

public record ToggleCommentVoteDTO(
        @IsId Long commentID,
        VoteTypeEnum type

) {
}
