package com.blog.writeapi.services.interfaces;

import com.blog.writeapi.models.CategoryModel;
import com.blog.writeapi.models.UserCategoryPreferenceModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;

import java.util.Optional;

public interface IUserCategoryPreferenceService {
    Optional<UserCategoryPreferenceModel> getByUserAndCategory(
            @IsModelInitialized UserModel user,
            @IsModelInitialized CategoryModel category
    );
    void delete(@IsModelInitialized UserCategoryPreferenceModel model);
    UserCategoryPreferenceModel create(
            @IsModelInitialized UserModel user,
            @IsModelInitialized CategoryModel category
    );
}
