package com.blog.writeapi.dtos.userProfile;

import com.blog.writeapi.models.enums.profile.ProfileVisibilityEnum;
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