package com.blog.writeapi.services.interfaces;

import com.blog.writeapi.models.TagModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.models.UserTagPreferenceModel;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;

import java.util.Optional;

public interface IUserTagPreferenceService {
    void delete(@IsModelInitialized UserTagPreferenceModel model);
    Optional<UserTagPreferenceModel> getByUserAndTag(
            @IsModelInitialized UserModel user,
            @IsModelInitialized TagModel tag
    );
    UserTagPreferenceModel create(
            @IsModelInitialized UserModel user,
            @IsModelInitialized TagModel tag
    );
}
