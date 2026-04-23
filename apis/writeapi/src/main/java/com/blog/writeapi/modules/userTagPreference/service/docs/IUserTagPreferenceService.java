package com.blog.writeapi.modules.userTagPreference.service.docs;

import com.blog.writeapi.modules.tag.models.TagModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.userTagPreference.models.UserTagPreferenceModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.classes.ResultToggle;

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
    ResultToggle<UserTagPreferenceModel> toggle(
            @IsId Long userId,
            @IsId Long tagId
    );
}
