package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.configs.mapper.CentralMapperConfig;
import com.blog.writeapi.modules.commentReaction.dtos.CommentReactionDTO;
import com.blog.writeapi.modules.commentReaction.models.CommentReactionModel;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class,
        uses = {CommentMapper.class, UserMapper.class, ReactionMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CommentReactionMapper {

    @Mapping(target = "updateAt", source = "updatedAt")
    CommentReactionDTO toDTO(@NotNull CommentReactionModel model);

}
