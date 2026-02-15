package com.blog.writeapi.modules.user.dtos;

import com.blog.writeapi.utils.annotations.validations.global.emailConstraint.EmailConstraint;
import com.blog.writeapi.utils.annotations.validations.user.uniqueEmail.UniqueEmail;
import com.blog.writeapi.utils.annotations.validations.user.uniqueUsername.UniqueUsername;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;

public record CreateUserDTO(
        @Size(min = 5, max = 100)
        @NotBlank
        String name,

        @Size(min = 6, max = 100)
        @NotBlank
        @UniqueUsername
        String username,

        @EmailConstraint
        @UniqueEmail
        String email,

        @Size(min = 8, max = 60)
        @NotBlank
        String password
){}
