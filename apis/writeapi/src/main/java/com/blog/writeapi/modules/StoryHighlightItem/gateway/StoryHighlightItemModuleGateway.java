package com.blog.writeapi.modules.StoryHighlightItem.gateway;

import com.blog.writeapi.modules.stories.model.StoryModel;
import com.blog.writeapi.modules.stories.service.interfaces.IStoryService;
import com.blog.writeapi.modules.storyHighlight.model.StoryHighlightModel;
import com.blog.writeapi.modules.storyHighlight.service.interfaces.IStoryHighlightService;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.user.service.docs.IUserService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StoryHighlightItemModuleGateway {

    private final IUserService userService;
    private final IStoryService storyService;
    private final IStoryHighlightService storyHighlightService;

    public UserModel findUserById(@IsId Long id) {
        return userService.GetByIdSimple(id);
    }

    public StoryModel findStoryById(@IsId Long id) {
        return this.storyService.findById(id);
    }

    public StoryHighlightModel findHighlightById(@IsId Long id) {
        return this.storyHighlightService.findByIdSimple(id);
    }

    public void toggleStoryHighlight(StoryModel story) {
        storyService.toggleStoryHighlight(story);
    }

}
