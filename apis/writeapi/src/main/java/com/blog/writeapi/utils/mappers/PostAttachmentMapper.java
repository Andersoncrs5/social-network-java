package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.configs.mapper.CentralMapperConfig;
import com.blog.writeapi.modules.postAttachment.dtos.CreatePostAttachmentDTO;
import com.blog.writeapi.modules.postAttachment.dtos.PostAttachmentDTO;
import com.blog.writeapi.modules.postAttachment.dtos.UpdatePostAttachmentDTO;
import com.blog.writeapi.modules.postAttachment.models.PostAttachmentModel;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        uses = {PostMapper.class, UserMapper.class},
        config = CentralMapperConfig.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PostAttachmentMapper {

    PostAttachmentDTO toDTO(@IsModelInitialized PostAttachmentModel model);

    @Mapping(target = "post", ignore = true)
    PostAttachmentModel toModel(CreatePostAttachmentDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "uploader", ignore = true)
    @Mapping(target = "storageKey", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    void merge(UpdatePostAttachmentDTO dto, @MappingTarget PostAttachmentModel model);

}
