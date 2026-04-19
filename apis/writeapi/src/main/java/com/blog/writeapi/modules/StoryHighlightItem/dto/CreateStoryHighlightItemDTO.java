package com.blog.writeapi.modules.StoryHighlightItem.dto;

import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;

public record CreateStoryHighlightItemDTO(
        @IsId Long highlightId,
        @IsId Long storyId
) {
}
