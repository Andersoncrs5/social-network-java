package com.blog.writeapi.modules.storyReaction.gateway;

import com.blog.writeapi.modules.reaction.models.ReactionModel;
import com.blog.writeapi.modules.reaction.service.docs.IReactionService;
import com.blog.writeapi.modules.stories.model.StoryModel;
import com.blog.writeapi.modules.stories.service.interfaces.IStoryService;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.user.service.docs.IUserService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Validated
@RequiredArgsConstructor
public class StoryReactionModuleGateway {

    private final IStoryService storyService;
    private final IUserService userService;
    private final IReactionService reactionService;

    public ReactionModel findReactionById(@IsId Long id) {
        return reactionService.getByIdSimple(id);
    }

    public UserModel findUserById(@IsId Long id) {
        return userService.GetByIdSimple(id);
    }

    public StoryModel findStoryById(@IsId Long id) {
        return storyService.findById(id);
    }

}
