package com.blog.writeapi.dtos.userProfile;

import com.blog.writeapi.dtos.user.UserDTO;
import com.blog.writeapi.models.enums.profile.ProfileVisibilityEnum;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;

import java.time.OffsetDateTime;
import java.util.Set;

public record UserProfileDTO(
        @IsId
        Long id,
        String bio,
        String avatarUrl,
        Set<String> websiteUrls,
        ProfileVisibilityEnum visibility,
        UserDTO user,
        Long version,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
