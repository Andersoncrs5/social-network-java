package com.blog.writeapi.modules.user.dtos;

import java.time.LocalDateTime;

public record UserDTO(
        Long id,
        String name,
        String username,
        String email,
        LocalDateTime createdAt
) {
}
