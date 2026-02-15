package com.blog.writeapi.dtos.userCategoryPreference;

import com.blog.writeapi.dtos.category.CategoryDTO;
import com.blog.writeapi.dtos.user.UserDTO;

import java.time.OffsetDateTime;

public record UserCategoryPreferenceDTO(
        Long id,
        UserDTO user,
        CategoryDTO category,
        Double interestScore,
        Long version,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
