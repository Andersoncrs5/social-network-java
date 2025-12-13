package com.blog.writeapi.dtos.user;

import java.time.LocalDateTime;

public record UserDTO(
        Long id,
        String name,
        String username,
        String email,
        LocalDateTime createdAt
) {
}
