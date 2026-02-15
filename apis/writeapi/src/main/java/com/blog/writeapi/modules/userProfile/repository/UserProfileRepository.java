package com.blog.writeapi.modules.userProfile.repository;

import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.userProfile.models.UserProfileModel;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<@NonNull UserProfileModel, @NonNull Long> {
    Boolean existsByUser(@IsModelInitialized UserModel user);

    Optional<UserProfileModel> findByUser(@IsModelInitialized UserModel user);
    
}
