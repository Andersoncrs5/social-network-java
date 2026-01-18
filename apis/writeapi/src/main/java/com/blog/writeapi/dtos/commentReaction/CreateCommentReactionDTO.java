package com.blog.writeapi.dtos.commentReaction;

import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;

public record CreateCommentReactionDTO(
        @IsId Long commentID,
        @IsId Long reactionID
) {
}
