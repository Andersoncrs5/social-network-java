package com.blog.writeapi.modules.storyView.service.interfaces;

import com.blog.writeapi.modules.storyView.model.StoryViewModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;

public interface IStoryViewService {
    StoryViewModel findByIdSimple(@IsId Long id);
    void delete(@IsId Long id);
    StoryViewModel createView(
            @IsId Long userId,
            @IsId Long storyId
    );
    boolean createIfNotExists(
            @IsId Long userId,
            @IsId Long storyId
    );
}
