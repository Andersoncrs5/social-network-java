package com.blog.writeapi.dtos.postReaction;

import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;

public record CreatePostReactionDTO(
        @IsId Long postId,
        @IsId Long reactionId
) {
}
