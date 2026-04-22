package com.blog.writeapi.modules.postShare.gateway;

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
public class PostShareModuleGateway {

    private final IUserService iUserService;
    private final IPostService iPostService;
    private final IUserBlockService userBlockService;

    public boolean isBlocked(
            @IsId Long blockerId,
            @IsId Long blockedId
    ) {
        return this.userBlockService.isBlocked(blockerId, blockedId);
    }

    public UserModel findUserById(@IsId Long id) {
        return this.iUserService.GetByIdSimple(id);
    }

    public PostModel findPostById(@IsId Long id) {
        return this.iPostService.getByIdSimple(id);
    }

}
