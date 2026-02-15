package com.blog.writeapi.modules.user.dtos;

import jakarta.validation.constraints.Size;

public record UpdateUserDTO(
        @Size(min = 5, max = 100)
        String name,
        @Size(min = 6, max = 100)
        String username,
        @Size(min = 8, max = 60)
        String password
){

}
