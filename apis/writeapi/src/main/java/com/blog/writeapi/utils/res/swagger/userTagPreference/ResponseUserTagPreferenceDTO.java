package com.blog.writeapi.utils.res.swagger.userTagPreference;

import com.blog.writeapi.dtos.userTagPreference.UserTagPreferenceDTO;
import com.blog.writeapi.utils.res.ResponseHttp;

public record ResponseUserTagPreferenceDTO(
        ResponseHttp<UserTagPreferenceDTO> dtoResponseHttp
) {
}
