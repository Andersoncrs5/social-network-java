package com.blog.writeapi.modules.commentAttachment.service.docs;

import com.blog.writeapi.modules.commentAttachment.dtos.CreateCommentAttachmentDTO;
import com.blog.writeapi.modules.commentAttachment.dtos.UpdateCommentAttachmentDTO;
import com.blog.writeapi.modules.commentAttachment.models.CommentAttachmentModel;
import com.blog.writeapi.modules.comment.models.CommentModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;

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
