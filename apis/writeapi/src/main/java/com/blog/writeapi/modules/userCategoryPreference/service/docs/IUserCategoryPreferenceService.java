package com.blog.writeapi.modules.userCategoryPreference.service.docs;

import com.blog.writeapi.modules.category.models.CategoryModel;
import com.blog.writeapi.modules.userCategoryPreference.models.UserCategoryPreferenceModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;

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
