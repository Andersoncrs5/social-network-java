package com.blog.writeapi.modules.commentReaction.dtos;

import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;

public record CreateCommentReactionDTO(
        @IsId Long commentID,
        @IsId Long reactionID
) {
}
