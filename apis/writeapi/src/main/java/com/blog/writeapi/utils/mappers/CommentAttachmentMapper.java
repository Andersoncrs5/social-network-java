package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.configs.mapper.CentralMapperConfig;
import com.blog.writeapi.modules.comment.models.CommentModel;
import com.blog.writeapi.modules.commentAttachment.dtos.CommentAttachmentDTO;
import com.blog.writeapi.modules.commentAttachment.dtos.CreateCommentAttachmentDTO;
import com.blog.writeapi.modules.commentAttachment.dtos.UpdateCommentAttachmentDTO;
import com.blog.writeapi.modules.commentAttachment.models.CommentAttachmentModel;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class,
        uses = {CommentModel.class, UserMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CommentAttachmentMapper {

    CommentAttachmentDTO toDTO(@IsModelInitialized CommentAttachmentModel model);

    @Mapping(target = "comment", ignore = true)
    CommentAttachmentModel toModel(CreateCommentAttachmentDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "comment", ignore = true)
    @Mapping(target = "uploader", ignore = true)
    @Mapping(target = "storageKey", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    void merge(UpdateCommentAttachmentDTO dto, @MappingTarget CommentAttachmentModel model);

}
