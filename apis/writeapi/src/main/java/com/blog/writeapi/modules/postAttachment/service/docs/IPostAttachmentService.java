package com.blog.writeapi.modules.postAttachment.service.docs;

import com.blog.writeapi.modules.postAttachment.dtos.CreatePostAttachmentDTO;
import com.blog.writeapi.modules.postAttachment.dtos.UpdatePostAttachmentDTO;
import com.blog.writeapi.modules.postAttachment.models.PostAttachmentModel;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;

import java.util.Optional;

public interface IPostAttachmentService {
    PostAttachmentModel getByIdSimple(@IsId Long id);
    Boolean delete(@IsModelInitialized PostAttachmentModel model, @IsId Long userId);
    Optional<PostAttachmentModel> create(
            CreatePostAttachmentDTO dto,
            @IsModelInitialized UserModel user,
            @IsModelInitialized PostModel post
    );
    PostAttachmentModel updateMetadata(@IsModelInitialized PostAttachmentModel model, UpdatePostAttachmentDTO dto);
    void deleteAllByPost(@IsModelInitialized PostModel post);
}
