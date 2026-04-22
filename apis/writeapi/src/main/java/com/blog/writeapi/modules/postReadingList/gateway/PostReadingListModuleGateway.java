package com.blog.writeapi.modules.postReadingList.gateway;

import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.post.services.interfaces.IPostService;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.user.service.docs.IUserService;
import com.blog.writeapi.modules.userBlock.service.docs.IUserBlockService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostReadingListModuleGateway {

    private final IUserService userService;
    private final IPostService postService;
    private final IUserBlockService userBlockService;

    public boolean isBlocked(
            @IsId Long blockerId,
            @IsId Long blockedId
    ) {
        return this.userBlockService.isBlocked(blockerId, blockedId);
    }

    public UserModel findUserById(@IsId Long id) {
        return this.userService.GetByIdSimple(id);
    }

    public PostModel findPostById(@IsId Long id) {
        return this.postService.getByIdSimple(id);
    }

}
