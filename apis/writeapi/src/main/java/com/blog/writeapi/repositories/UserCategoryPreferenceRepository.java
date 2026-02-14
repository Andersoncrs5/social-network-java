package com.blog.writeapi.repositories;

import com.blog.writeapi.models.CategoryModel;
import com.blog.writeapi.models.UserCategoryPreferenceModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCategoryPreferenceRepository extends JpaRepository<UserCategoryPreferenceModel, Long> {
    Boolean existsByUserAndCategory(
            @IsModelInitialized UserModel user,
            @IsModelInitialized CategoryModel category
    );
    Optional<UserCategoryPreferenceModel> findByUserAndCategory(
            @IsModelInitialized UserModel user,
            @IsModelInitialized CategoryModel category
    );
}
