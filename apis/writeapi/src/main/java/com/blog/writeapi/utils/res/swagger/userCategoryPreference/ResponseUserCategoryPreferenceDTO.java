package com.blog.writeapi.utils.res.swagger.userCategoryPreference;

import com.blog.writeapi.modules.userCategoryPreference.dtos.UserCategoryPreferenceDTO;
import com.blog.writeapi.utils.res.ResponseHttp;

public record ResponseUserCategoryPreferenceDTO(
        ResponseHttp<UserCategoryPreferenceDTO> dto
) {
}
