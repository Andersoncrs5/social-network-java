package com.blog.writeapi.services.interfaces;

import com.blog.writeapi.models.CommentAttachmentModel;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;

public interface ICommentAttachmentService {
    boolean delete(@IsModelInitialized CommentAttachmentModel attachment);
    CommentAttachmentModel findByIdSimple(@IsId Long id);
}
