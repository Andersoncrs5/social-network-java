package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.configs.mapper.CentralMapperConfig;
import com.blog.writeapi.modules.postReaction.dtos.PostReactionDTO;
import com.blog.writeapi.modules.postReaction.models.PostReactionModel;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class,
        uses = {PostMapper.class, UserMapper.class, ReactionMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PostReactionMapper {

    @Mapping(target = "updateAt", source = "updatedAt")
    PostReactionDTO toDTO(@NotNull PostReactionModel model);
}