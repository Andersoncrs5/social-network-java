package com.blog.writeapi.modules.storyReaction.service.interfaces;

import com.blog.writeapi.modules.storyReaction.model.StoryReactionModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.classes.ResultToggle;

public interface IStoryReactionService {
    @Deprecated
    ResultToggle<StoryReactionModel> react(
            @IsId Long userId,
            @IsId Long storyId,
            @IsId Long reactionId
    );
    ResultToggle<StoryReactionModel> react1(
            @IsId Long userId,
            @IsId Long storyId,
            @IsId Long reactionId
    );
}
