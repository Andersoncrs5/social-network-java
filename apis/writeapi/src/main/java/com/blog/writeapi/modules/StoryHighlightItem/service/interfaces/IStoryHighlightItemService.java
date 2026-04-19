package com.blog.writeapi.modules.StoryHighlightItem.service.interfaces;

import com.blog.writeapi.modules.StoryHighlightItem.dto.CreateStoryHighlightItemDTO;
import com.blog.writeapi.modules.StoryHighlightItem.model.StoryHighlightItemModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.classes.ResultToggle;

public interface IStoryHighlightItemService {
    ResultToggle<StoryHighlightItemModel> toggle(
            @IsId Long userId,
            CreateStoryHighlightItemDTO dto
    );
}
