package com.blog.writeapi.modules.userCategoryPreference.dtos;

import com.blog.writeapi.modules.category.dtos.CategoryDTO;
import com.blog.writeapi.modules.user.dtos.UserDTO;

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
