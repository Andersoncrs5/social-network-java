package com.blog.writeapi.modules.storyReaction.dto;

import com.blog.writeapi.modules.reaction.dtos.ReactionDTO;
import com.blog.writeapi.modules.stories.dto.StoryDTO;
import com.blog.writeapi.modules.user.dtos.UserDTO;

import java.time.OffsetDateTime;

public record StoryReactionDTO(
        Long id,
        UserDTO user,
        StoryDTO story,
        ReactionDTO reaction,
        Long version,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
