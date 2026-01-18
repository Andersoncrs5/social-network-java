package com.blog.writeapi.services.interfaces;

import com.blog.writeapi.dtos.userProfile.UpdateUserProfileDTO;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.models.UserProfileModel;

import java.util.Optional;

public interface IUserProfileService {
    Boolean existsByUser(UserModel user);
    Optional<UserProfileModel> getByUser(UserModel user);
    void delete(UserProfileModel profile);
    UserProfileModel create(UserModel user);
    UserProfileModel update(UserProfileModel model, UpdateUserProfileDTO dto);
}
