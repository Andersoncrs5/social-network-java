package com.blog.writeapi.modules.userProfile.service.docs;

import com.blog.writeapi.modules.userProfile.dtos.UpdateUserProfileDTO;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.userProfile.models.UserProfileModel;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;

import java.util.Optional;

public interface IUserProfileService {
    Boolean existsByUser(@IsModelInitialized UserModel user);
    Optional<UserProfileModel> getByUser(@IsModelInitialized UserModel user);
    UserProfileModel getByUserSimple(@IsModelInitialized UserModel user);
    void delete(@IsModelInitialized UserProfileModel profile);
    UserProfileModel create(@IsModelInitialized UserModel user);
    UserProfileModel update(@IsModelInitialized UserProfileModel model, UpdateUserProfileDTO dto);
}
