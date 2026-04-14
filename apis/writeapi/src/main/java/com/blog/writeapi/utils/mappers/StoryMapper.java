package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.configs.mapper.CentralMapperConfig;
import com.blog.writeapi.modules.stories.dto.CreateStoryDTO;
import com.blog.writeapi.modules.stories.dto.StoryDTO;
import com.blog.writeapi.modules.stories.model.StoryModel;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.*;

@Mapper(
        config = CentralMapperConfig.class,
        componentModel = "spring",
        uses = { UserMapper.class }
)
public interface StoryMapper {

    StoryDTO toDTO(@IsModelInitialized StoryModel story);

    StoryModel toModel(@NotNull StoryDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "expiresAt", ignore = true)
    @Mapping(target = "isArchived", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    StoryModel toModel(@NotNull CreateStoryDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "expiresAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void merge(@NotNull StoryDTO dto, @MappingTarget @IsModelInitialized StoryModel story);
}