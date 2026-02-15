package com.blog.writeapi.utils.res.swagger.user;

import com.blog.writeapi.modules.user.dtos.UserDTO;
import com.blog.writeapi.utils.res.ResponseHttp;

public record ResponseUserDTO(
        ResponseHttp<UserDTO> res
) {
}
