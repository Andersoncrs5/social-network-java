package com.blog.writeapi.modules.userTagPreference.dtos;

import com.blog.writeapi.modules.tag.dtos.TagDTO;
import com.blog.writeapi.modules.user.dtos.UserDTO;

import java.time.OffsetDateTime;

public record UserTagPreferenceDTO(
        Long id,
        UserDTO user,
        TagDTO tag,
        Double interestScore,
        Long version,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
