package com.blog.writeapi.modules.commentFavorite.service.docs;

import com.blog.writeapi.modules.commentFavorite.models.CommentFavoriteModel;
import com.blog.writeapi.modules.comment.models.CommentModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;

import java.util.Optional;

public interface ICommentFavoriteService {
    CommentFavoriteModel add(
            @IsModelInitialized UserModel user,
            @IsModelInitialized CommentModel comment
    );
    CommentFavoriteModel getById(@IsId Long id);
    CommentFavoriteModel getByCommentIdAndUserId(
            @IsModelInitialized CommentModel comment,
            @IsModelInitialized UserModel user
    );
    Optional<CommentFavoriteModel> findByCommentIdAndUserId(
            @IsModelInitialized CommentModel comment,
            @IsModelInitialized UserModel user
    );
    void remove(@IsModelInitialized CommentFavoriteModel model);
}
