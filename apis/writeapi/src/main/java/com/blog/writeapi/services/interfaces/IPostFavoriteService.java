package com.blog.writeapi.services.interfaces;

import com.blog.writeapi.models.PostFavoriteModel;
import com.blog.writeapi.models.PostModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;

import java.util.Optional;

public interface IPostFavoriteService {
    PostFavoriteModel getByIdSimple(@IsId Long id);
    void delete(@IsModelInitialized PostFavoriteModel model);
    PostFavoriteModel create(
            @IsModelInitialized PostModel post,
            @IsModelInitialized UserModel user
    );
    Boolean existsByPostAndUser(
            @IsModelInitialized PostModel post,
            @IsModelInitialized UserModel user
    );
    Optional<PostFavoriteModel> getByPostAndUser(
            @IsModelInitialized PostModel post,
            @IsModelInitialized UserModel user
    );
}
