package com.blog.writeapi.services.interfaces;

import com.blog.writeapi.models.PostFavoriteModel;
import com.blog.writeapi.models.PostModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;

public interface IPostFavoriteService {
    PostFavoriteModel getByIdSimple(@IsId Long id);
    void delete(PostFavoriteModel model);
    PostFavoriteModel create(PostModel post, UserModel user);
    Boolean existsByPostAndUser(PostModel post, UserModel user);
}
