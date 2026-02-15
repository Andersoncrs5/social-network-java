package com.blog.writeapi.modules.postCategory.service.docs;

import com.blog.writeapi.modules.postCategory.dtos.CreatePostCategoriesDTO;
import com.blog.writeapi.modules.postCategory.dtos.UpdatePostCategoriesDTO;
import com.blog.writeapi.modules.category.models.CategoryModel;
import com.blog.writeapi.modules.postCategory.models.PostCategoriesModel;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
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
