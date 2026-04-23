package com.blog.writeapi.modules.userTagPreference.gateway;

import com.blog.writeapi.modules.tag.models.TagModel;
import com.blog.writeapi.modules.tag.service.docs.ITagService;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.user.service.docs.IUserService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserTagPreferenceModuleGateway {

    private final IUserService userService;
    private final ITagService tagService;

    public UserModel findUserById(@IsId Long id) {
        return userService.GetByIdSimple(id);
    }

    public TagModel findTagById(@IsId Long id) {
        return tagService.getByIdSimple(id);
    }

}
