package com.blog.writeapi.services.interfaces;

import com.blog.writeapi.dtos.postCategories.CreatePostCategoriesDTO;
import com.blog.writeapi.dtos.postCategories.UpdatePostCategoriesDTO;
import com.blog.writeapi.models.CategoryModel;
import com.blog.writeapi.models.PostCategoriesModel;
import com.blog.writeapi.models.PostModel;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

public interface IPostCategoriesService {
    Boolean existsByPostAndCategory(
            @NotNull PostModel post,
            @NotNull CategoryModel category
    );
    boolean existsByPostIdAndPrimaryTrue(@IsId Long postId);
    Optional<PostCategoriesModel> getById(@IsId Long id);
    PostCategoriesModel getByIdSimple(@IsId Long id);
    void delete(@NotNull PostCategoriesModel model);
    PostCategoriesModel create(
            @NotNull CreatePostCategoriesDTO dto,
            @NotNull PostModel post,
            @NotNull CategoryModel category
    );
    PostCategoriesModel update(
            @NotNull UpdatePostCategoriesDTO dto,
            @NotNull PostCategoriesModel model
    );
}
