package com.blog.writeapi.modules.userTagPreference.repository;

import com.blog.writeapi.modules.tag.models.TagModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.userTagPreference.models.UserTagPreferenceModel;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserTagPreferenceRepository extends JpaRepository<UserTagPreferenceModel, Long> {
    Optional<UserTagPreferenceModel> findByUserAndTag(
            @IsModelInitialized UserModel user,
            @IsModelInitialized TagModel tag
    );
}
