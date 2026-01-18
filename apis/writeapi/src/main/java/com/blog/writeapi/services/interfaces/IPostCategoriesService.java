package com.blog.writeapi.services.interfaces;

import com.blog.writeapi.dtos.postCategories.CreatePostCategoriesDTO;
import com.blog.writeapi.dtos.postCategories.UpdatePostCategoriesDTO;
import com.blog.writeapi.models.CategoryModel;
import com.blog.writeapi.models.PostCategoriesModel;
import com.blog.writeapi.models.PostModel;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

public interface IPostCategoriesService {
    PostCategoriesModel updatev2(
            @NotNull UpdatePostCategoriesDTO dto,
            @IsModelInitialized PostCategoriesModel model
    );
    Boolean existsByPostAndCategory(
            @IsModelInitialized PostModel post,
            @IsModelInitialized CategoryModel category
    );
    boolean existsByPostIdAndPrimaryTrue(@IsId Long postId);
    Optional<PostCategoriesModel> getById(@IsId Long id);
    PostCategoriesModel getByIdSimple(@IsId Long id);
    void delete(@IsModelInitialized PostCategoriesModel model);
    PostCategoriesModel create(
            @NotNull CreatePostCategoriesDTO dto,
            @IsModelInitialized PostModel post,
            @IsModelInitialized CategoryModel category
    );
    @Deprecated
    PostCategoriesModel update(
            @NotNull UpdatePostCategoriesDTO dto,
            @IsModelInitialized PostCategoriesModel model
    );
}
