package com.blog.writeapi.modules.stories.service.interfaces;

import com.blog.writeapi.modules.stories.dto.CreateStoryDTO;
import com.blog.writeapi.modules.stories.model.StoryModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;

public interface IStoryService {
    StoryModel create(
            @IsId Long userId,
            CreateStoryDTO dto
    );
    boolean delete(@IsModelInitialized StoryModel story);
    StoryModel findById(@IsId Long id);
    void archiveExpiredStories();
    void archiveExpiredStoriesPageable();
    void toggleStoryHighlight(StoryModel model);
}
