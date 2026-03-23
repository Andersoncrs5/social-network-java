package com.blog.writeapi.modules.userSettings.service.interfaces;

import com.blog.writeapi.modules.userSettings.dto.UpdateUserSettingsDTO;
import com.blog.writeapi.modules.userSettings.model.UserSettingsModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;

public interface IUserSettingsService {
    UserSettingsModel findByUserIdSimple(
            @IsId Long userId
    );
    UserSettingsModel create(
            @IsId Long userId
    );
    UserSettingsModel update(
            UpdateUserSettingsDTO dto,
            @IsModelInitialized UserSettingsModel setting
    );
}
