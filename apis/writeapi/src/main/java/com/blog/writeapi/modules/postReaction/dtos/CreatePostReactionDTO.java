package com.blog.writeapi.modules.postReaction.dtos;

import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;

public record CreatePostReactionDTO(
        @IsId Long postId,
        @IsId Long reactionId
) {
}
