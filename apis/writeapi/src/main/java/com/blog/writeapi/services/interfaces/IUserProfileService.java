package com.blog.writeapi.services.interfaces;

import com.blog.writeapi.dtos.userProfile.UpdateUserProfileDTO;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.models.UserProfileModel;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;

import java.util.Optional;

public interface IUserProfileService {
    Boolean existsByUser(@IsModelInitialized UserModel user);
    Optional<UserProfileModel> getByUser(@IsModelInitialized UserModel user);
    UserProfileModel getByUserSimple(@IsModelInitialized UserModel user);
    void delete(@IsModelInitialized UserProfileModel profile);
    UserProfileModel create(@IsModelInitialized UserModel user);
    UserProfileModel update(@IsModelInitialized UserProfileModel model, UpdateUserProfileDTO dto);
}
