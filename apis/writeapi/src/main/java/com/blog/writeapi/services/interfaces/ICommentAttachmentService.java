package com.blog.writeapi.services.interfaces;

import com.blog.writeapi.dtos.commentAttachment.CreateCommentAttachmentDTO;
import com.blog.writeapi.dtos.commentAttachment.UpdateCommentAttachmentDTO;
import com.blog.writeapi.models.CommentAttachmentModel;
import com.blog.writeapi.models.CommentModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;

import java.util.Optional;

public interface ICommentAttachmentService {
    boolean delete(@IsModelInitialized CommentAttachmentModel attachment);
    CommentAttachmentModel findByIdSimple(@IsId Long id);
    Optional<CommentAttachmentModel> create(
            CreateCommentAttachmentDTO dto,
            @IsModelInitialized UserModel user,
            @IsModelInitialized CommentModel comment
    );
    CommentAttachmentModel update(@IsModelInitialized CommentAttachmentModel model, UpdateCommentAttachmentDTO dto);
}
