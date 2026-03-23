package com.blog.writeapi.utils.res;

import com.blog.writeapi.modules.user.dtos.UserDTO;
import com.blog.writeapi.modules.userSettings.dto.UserSettingsDTO;

import java.util.List;

public record ResponseTokens(
        String token,
        String refreshToken,
        UserDTO user,
        UserSettingsDTO settingsDTO,
        List<String> roles
) {
}
