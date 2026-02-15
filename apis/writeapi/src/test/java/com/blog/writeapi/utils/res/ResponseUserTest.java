package com.blog.writeapi.utils.res;

import com.blog.writeapi.modules.user.dtos.CreateUserDTO;
import com.blog.writeapi.modules.user.dtos.UserDTO;

public record ResponseUserTest(
        ResponseTokens tokens,
        CreateUserDTO dto,
        UserDTO userDTO
) {
}
