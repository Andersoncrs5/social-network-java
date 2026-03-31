package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.configs.mapper.CentralMapperConfig;
import com.blog.writeapi.modules.pinnedPost.dto.CreatePinnedPostDTO;
import com.blog.writeapi.modules.pinnedPost.dto.PinnedPostDTO;
import com.blog.writeapi.modules.pinnedPost.dto.UpdatePinnedPostDTO;
import com.blog.writeapi.modules.pinnedPost.model.PinnedPostModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;


@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class,
        uses = {
                UserMapper.class,
                PostMapper.class
        },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PinnedPostMapper {

    PinnedPostDTO toDTO(PinnedPostModel model);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "post.id", source = "postId")
    PinnedPostModel toModel(CreatePinnedPostDTO dto);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateModel(UpdatePinnedPostDTO dto, @MappingTarget PinnedPostModel model);

}
