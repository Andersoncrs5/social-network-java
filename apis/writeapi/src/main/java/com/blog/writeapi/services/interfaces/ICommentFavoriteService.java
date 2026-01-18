package com.blog.writeapi.services.interfaces;

import com.blog.writeapi.models.CommentFavoriteModel;
import com.blog.writeapi.models.CommentModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;

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
