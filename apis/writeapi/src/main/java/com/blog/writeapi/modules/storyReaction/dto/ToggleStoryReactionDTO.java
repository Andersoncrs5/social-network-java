package com.blog.writeapi.modules.storyReaction.dto;

import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;

public record ToggleStoryReactionDTO(
        @IsId Long reactionId,
        @IsId Long storyId
) {
}
