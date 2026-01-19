package com.blog.writeapi.repositories;

import com.blog.writeapi.models.CategoryModel;
import com.blog.writeapi.models.PostCategoriesModel;
import com.blog.writeapi.models.PostModel;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostCategoriesRepository extends JpaRepository<@NonNull PostCategoriesModel, @NonNull Long> {
    boolean existsByPostIdAndPrimaryTrue(@IsId Long value);

    Boolean existsByPostAndCategory(@IsModelInitialized PostModel post, @IsModelInitialized CategoryModel category);

    Optional<PostCategoriesModel> findByPrimaryTrueAndPost(@IsModelInitialized PostModel post);
}
