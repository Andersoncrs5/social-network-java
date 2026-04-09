package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.configs.mapper.CentralMapperConfig;
import com.blog.writeapi.modules.comment.dtos.CommentDTO;
import com.blog.writeapi.modules.comment.dtos.CreateCommentDTO;
import com.blog.writeapi.modules.comment.dtos.UpdateCommentDTO;
import com.blog.writeapi.modules.comment.models.CommentModel;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.*;

@Mapper(
        config = CentralMapperConfig.class,
        componentModel = "spring",
        uses = { UserMapper.class, PostMapper.class }
)
public interface CommentMapper {

    @Mapping(target = "user", source = "author")
    @Mapping(target = "parentId", source = "parent.id")
    CommentDTO toDTO(@NotNull CommentModel model);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "replies", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "edited", ignore = true)
    @Mapping(target = "pinned", ignore = true)
    @Mapping(target = "ipAddress", ignore = true)
    CommentModel toModel(@NotNull CreateCommentDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "replies", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "ipAddress", ignore = true)
    void merge(UpdateCommentDTO dto, @MappingTarget CommentModel model);

}
