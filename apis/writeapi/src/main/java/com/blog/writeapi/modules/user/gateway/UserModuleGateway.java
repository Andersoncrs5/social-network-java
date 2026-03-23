package com.blog.writeapi.modules.user.gateway;

import com.blog.writeapi.modules.userSettings.model.UserSettingsModel;
import com.blog.writeapi.modules.userSettings.service.interfaces.IUserSettingsService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserModuleGateway {
    private final IUserSettingsService userSettingsService;

    public UserSettingsModel createUserSettings(@IsId Long userID) {
        return this.userSettingsService.create(userID);
    }

}
