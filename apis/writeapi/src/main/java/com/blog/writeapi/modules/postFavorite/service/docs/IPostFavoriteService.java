package com.blog.writeapi.modules.postFavorite.service.docs;

import com.blog.writeapi.modules.postFavorite.models.PostFavoriteModel;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.classes.ResultToggle;

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
    ResultToggle<PostFavoriteModel> toggle(
            @IsModelInitialized PostModel post,
            @IsModelInitialized UserModel user
    );
}
