package com.blog.writeapi.utils.res;

import com.blog.writeapi.dtos.user.CreateUserDTO;
import com.blog.writeapi.dtos.user.UserDTO;

public record ResponseUserTest(
        ResponseTokens tokens,
        CreateUserDTO dto,
        UserDTO userDTO
) {
}
