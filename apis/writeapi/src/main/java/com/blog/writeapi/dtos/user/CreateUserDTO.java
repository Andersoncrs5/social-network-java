package com.blog.writeapi.dtos.user;

import com.blog.writeapi.utils.annotations.valid.user.uniqueEmail.UniqueEmail;
import com.blog.writeapi.utils.annotations.valid.user.uniqueUsername.UniqueUsername;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;

public record CreateUserDTO(
        @Max(100)
        @NotBlank
        String name,

        @Max(100)
        @NotBlank
        @UniqueUsername
        String username,

        @Max(150)
        @NotBlank
        @UniqueEmail
        String email,

        @Max(60)
        @NotBlank
        String password
){}
