package com.blog.writeapi.dtos.userProfile;

import com.blog.writeapi.dtos.user.UserDTO;
import com.blog.writeapi.models.enums.profile.ProfileVisibilityEnum;

import java.util.Set;

public record UpdateUserProfileDTO(
        String bio,
        String avatarUrl,
        Set<String> websiteUrls,
        ProfileVisibilityEnum visibility
) {
}