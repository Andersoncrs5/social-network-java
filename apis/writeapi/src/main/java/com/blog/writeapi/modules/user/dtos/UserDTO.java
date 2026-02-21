package com.blog.writeapi.modules.user.dtos;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public record UserDTO(
        Long id,
        String name,
        String username,
        String email,
        String bannerUrl,
        OffsetDateTime loginBlockAt,
        LocalDateTime createdAt
) {
}
