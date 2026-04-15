package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.configs.mapper.CentralMapperConfig;
import com.blog.writeapi.modules.storyReaction.dto.StoryReactionDTO;
import com.blog.writeapi.modules.storyReaction.model.StoryReactionModel;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import org.mapstruct.Mapper;

@Mapper(
        config = CentralMapperConfig.class,
        componentModel = "spring",
        uses = {
                UserMapper.class,
                StoryMapper.class,
                ReactionMapper.class
        }
)
public interface StoryReactionMapper {

    StoryReactionDTO toDTO(@IsModelInitialized StoryReactionModel storyReaction);
}
