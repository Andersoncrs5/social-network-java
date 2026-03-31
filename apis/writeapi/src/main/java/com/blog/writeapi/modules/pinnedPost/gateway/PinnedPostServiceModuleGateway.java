package com.blog.writeapi.modules.pinnedPost.gateway;

import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.post.services.interfaces.IPostService;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.user.service.docs.IUserService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PinnedPostServiceModuleGateway {

    private final IUserService userService;
    private final IPostService postService;

    public UserModel findUserById(@IsId Long id) {
        return userService.GetByIdSimple(id);
    }

    public PostModel findPostById(@IsId Long id) {
        return postService.getByIdSimple(id);
    }

}
