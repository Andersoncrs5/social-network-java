package com.blog.writeapi.modules.storyHighlight.service.interfaces;

import com.blog.writeapi.modules.storyHighlight.dto.CreateStoryHighlightDTO;
import com.blog.writeapi.modules.storyHighlight.dto.UpdateStoryHighlightDTO;
import com.blog.writeapi.modules.storyHighlight.model.StoryHighlightModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.result.Result;
import jakarta.validation.constraints.NotNull;

public interface IStoryHighlightService {
    StoryHighlightModel findByIdSimple(@IsId Long id);
    boolean delete(
            @IsModelInitialized StoryHighlightModel highlight
    );
    StoryHighlightModel create(
            @IsId Long userId,
            @NotNull CreateStoryHighlightDTO dto
    );
    Result<StoryHighlightModel> update(
            @IsModelInitialized UserModel user,
            @IsModelInitialized StoryHighlightModel highlight,
            @NotNull UpdateStoryHighlightDTO dto
    );
}
