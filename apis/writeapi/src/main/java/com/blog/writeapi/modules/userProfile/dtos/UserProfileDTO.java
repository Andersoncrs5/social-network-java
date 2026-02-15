package com.blog.writeapi.modules.userProfile.dtos;

import com.blog.writeapi.modules.user.dtos.UserDTO;
import com.blog.writeapi.utils.enums.profile.ProfileVisibilityEnum;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;

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
