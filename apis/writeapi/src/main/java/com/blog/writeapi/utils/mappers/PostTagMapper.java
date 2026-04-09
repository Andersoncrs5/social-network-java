package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.configs.mapper.CentralMapperConfig;
import com.blog.writeapi.modules.postTag.dtos.CreatePostTagDTO;
import com.blog.writeapi.modules.postTag.dtos.PostTagDTO;
import com.blog.writeapi.modules.postTag.dtos.UpdatePostTagDTO;
import com.blog.writeapi.modules.postTag.models.PostTagModel;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.*;

@Mapper(
        config = CentralMapperConfig.class,
        componentModel = "spring",
        uses = { PostMapper.class, TagMapper.class }
)
public interface PostTagMapper {

    PostTagDTO toDTO(@NotNull PostTagModel model);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "tag", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PostTagModel toModel(@NotNull CreatePostTagDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "tag", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    void merge(UpdatePostTagDTO dto, @MappingTarget PostTagModel model);

}
