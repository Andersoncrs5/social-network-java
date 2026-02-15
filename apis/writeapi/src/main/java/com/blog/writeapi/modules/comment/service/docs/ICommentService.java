package com.blog.writeapi.modules.comment.service.docs;

import com.blog.writeapi.modules.comment.dtos.CreateCommentDTO;
import com.blog.writeapi.modules.comment.dtos.UpdateCommentDTO;
import com.blog.writeapi.modules.comment.models.CommentModel;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;

import java.util.Optional;

public interface ICommentService {
    Optional<CommentModel> getById(@IsId Long id);
    void delete(@IsModelInitialized CommentModel comment);
    CommentModel getByIdSimple(@IsId Long id);
    @Deprecated
    CommentModel create(
            CreateCommentDTO dto,
            @IsModelInitialized PostModel post,
            @IsModelInitialized UserModel user
    );
    CommentModel create(
            CreateCommentDTO dto,
            @IsModelInitialized PostModel post,
            @IsModelInitialized UserModel user,
            CommentModel comment
    );
    CommentModel update(
            UpdateCommentDTO dto,
            @IsModelInitialized CommentModel comment
    );
}
