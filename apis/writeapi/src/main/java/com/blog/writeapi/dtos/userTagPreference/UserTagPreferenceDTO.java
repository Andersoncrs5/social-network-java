package com.blog.writeapi.dtos.userTagPreference;

import com.blog.writeapi.dtos.tag.TagDTO;
import com.blog.writeapi.dtos.user.UserDTO;

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
