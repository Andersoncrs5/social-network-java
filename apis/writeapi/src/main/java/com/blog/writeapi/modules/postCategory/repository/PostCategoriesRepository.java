package com.blog.writeapi.modules.postCategory.repository;

import com.blog.writeapi.modules.category.models.CategoryModel;
import com.blog.writeapi.modules.postCategory.models.PostCategoriesModel;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostCategoriesRepository extends JpaRepository<@NonNull PostCategoriesModel, @NonNull Long> {
    boolean existsByPostIdAndPrimaryTrue(@IsId Long value);

    Boolean existsByPostAndCategory(@IsModelInitialized PostModel post, @IsModelInitialized CategoryModel category);

    Optional<PostCategoriesModel> findByPrimaryTrueAndPost(@IsModelInitialized PostModel post);

    Boolean existsByPostIdAndCategoryId(
            @IsId Long postId, @IsId
            Long categoryId
    );
}
