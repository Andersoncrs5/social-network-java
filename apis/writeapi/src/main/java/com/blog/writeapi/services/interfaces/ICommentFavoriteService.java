package com.blog.writeapi.services.interfaces;

import com.blog.writeapi.models.CommentFavoriteModel;
import com.blog.writeapi.models.CommentModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;

import java.util.Optional;

public interface ICommentFavoriteService {
    CommentFavoriteModel add(UserModel user, CommentModel comment);
    CommentFavoriteModel getById(@IsId Long id);
    CommentFavoriteModel getByCommentIdAndUserId(CommentModel comment, UserModel user);
    Optional<CommentFavoriteModel> findByCommentIdAndUserId(CommentModel comment, UserModel user);
    void remove(CommentFavoriteModel model);
}
