package com.blog.writeapi.modules.userSettings.gateway;

import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.user.service.docs.IUserService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class UserSettingsModuleGateway {
    private final IUserService userService;

    public UserSettingsModuleGateway(@Lazy IUserService userService) {
        this.userService = userService;
    }

    public UserModel findUserById(@IsId Long id) {
        return userService.GetByIdSimple(id);
    }

}
