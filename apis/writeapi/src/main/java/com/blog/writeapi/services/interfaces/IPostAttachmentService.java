package com.blog.writeapi.services.interfaces;

import com.blog.writeapi.dtos.postAttachment.CreatePostAttachmentDTO;
import com.blog.writeapi.dtos.postAttachment.UpdatePostAttachmentDTO;
import com.blog.writeapi.models.PostAttachmentModel;
import com.blog.writeapi.models.PostModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;

import java.util.Optional;

public interface IPostAttachmentService {
    PostAttachmentModel getByIdSimple(@IsId Long id);
    Boolean delete(@IsModelInitialized PostAttachmentModel model);
    Optional<PostAttachmentModel> create(CreatePostAttachmentDTO dto, @IsModelInitialized UserModel user, @IsModelInitialized PostModel post);
    PostAttachmentModel updateMetadata(Long id, UpdatePostAttachmentDTO dto);
}
