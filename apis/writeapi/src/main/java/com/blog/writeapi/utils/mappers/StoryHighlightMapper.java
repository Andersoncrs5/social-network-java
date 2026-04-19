package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.configs.mapper.CentralMapperConfig;
import com.blog.writeapi.modules.storyHighlight.dto.CreateStoryHighlightDTO;
import com.blog.writeapi.modules.storyHighlight.model.StoryHighlightModel;
import org.mapstruct.*;

import com.blog.writeapi.modules.storyHighlight.dto.StoryHighlightDTO;
import com.blog.writeapi.modules.storyHighlight.dto.UpdateStoryHighlightDTO;

@Mapper(
        config = CentralMapperConfig.class,
        componentModel = "spring",
        uses = { UserMapper.class }
)
public interface StoryHighlightMapper {

    StoryHighlightDTO toDTO(StoryHighlightModel model);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    StoryHighlightModel toModel(StoryHighlightDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    StoryHighlightModel toModel(CreateStoryHighlightDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "storageKey", ignore = true)
    @Mapping(target = "fileSize", ignore = true)
    @Mapping(target = "fileName", source = "fileName")
    @Mapping(target = "contentType", source = "contentType")
    @Mapping(target = "isPublic", source = "isPublic")
    @Mapping(target = "isVisible", source = "isVisible")
    @Mapping(target = "title", source = "title")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void merge(UpdateStoryHighlightDTO dto, @MappingTarget StoryHighlightModel model);
}