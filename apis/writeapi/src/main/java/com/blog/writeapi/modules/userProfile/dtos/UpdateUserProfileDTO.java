package com.blog.writeapi.modules.userProfile.dtos;

import com.blog.writeapi.utils.enums.profile.ProfileVisibilityEnum;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record UpdateUserProfileDTO(
        @Size(max = 800)
        String bio,

        @Size(max = 500)
        String avatarUrl,
        Set<String> websiteUrls,
        ProfileVisibilityEnum visibility
) {
}