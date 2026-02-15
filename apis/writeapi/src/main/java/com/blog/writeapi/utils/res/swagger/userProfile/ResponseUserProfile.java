package com.blog.writeapi.utils.res.swagger.userProfile;

import com.blog.writeapi.modules.userProfile.dtos.UserProfileDTO;
import com.blog.writeapi.utils.res.ResponseHttp;

public record ResponseUserProfile(
        ResponseHttp<UserProfileDTO> response
) {
}
