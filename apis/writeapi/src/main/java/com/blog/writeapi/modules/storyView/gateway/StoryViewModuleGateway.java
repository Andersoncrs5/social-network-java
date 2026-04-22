package com.blog.writeapi.modules.storyView.gateway;

import com.blog.writeapi.modules.stories.model.StoryModel;
import com.blog.writeapi.modules.stories.service.interfaces.IStoryService;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.user.service.docs.IUserService;
import com.blog.writeapi.modules.userBlock.service.docs.IUserBlockService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StoryViewModuleGateway {

    private final IUserService userService;
    private final IStoryService storyService;
    private final IUserBlockService userBlockService;

    public UserModel findUserById(@IsId Long id) {
        return userService.GetByIdSimple(id);
    }

    public StoryModel findStoryById(@IsId Long id) {
        return storyService.findById(id);
    }

    public boolean isBLocked(
            @IsId Long blockerId,
            @IsId Long blockedId
    ) {
        return this.userBlockService.isBlocked(blockerId, blockedId);
    }

}
