package com.blog.writeapi.modules.stories.gateway;

import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.user.service.docs.IUserService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StoryModuleGateway {

    private final IUserService userService;

    public UserModel findUserById(@IsId Long id) {
        return userService.GetByIdSimple(id);
    }

}
