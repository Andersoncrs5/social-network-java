package com.blog.writeapi.modules.commentVote.dtos;

import com.blog.writeapi.utils.enums.votes.VoteTypeEnum;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;

public record ToggleCommentVoteDTO(
        @IsId Long commentID,
        VoteTypeEnum type

) {
}
