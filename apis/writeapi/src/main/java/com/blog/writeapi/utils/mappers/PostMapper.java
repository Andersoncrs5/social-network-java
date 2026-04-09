package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.configs.mapper.CentralMapperConfig;
import com.blog.writeapi.modules.post.dtos.CreatePostDTO;
import com.blog.writeapi.modules.post.dtos.PostDTO;
import com.blog.writeapi.modules.post.dtos.UpdatePostDTO;
import com.blog.writeapi.modules.post.models.PostModel;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.*;

@Mapper(
        config = CentralMapperConfig.class,
        componentModel = "spring",
        uses = { UserMapper.class }
)
public interface PostMapper {

    @Mapping(target = "parent", ignore = true)
    PostModel toModel(@NotNull PostDTO dto);

    @Mapping(target = "parentId", source = "post.parent.id")
    PostDTO toDTO(@NotNull PostModel post);

    PostModel toModel(@NotNull CreatePostDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void merge(@NotNull UpdatePostDTO dto, @MappingTarget @NotNull PostModel post);

}
