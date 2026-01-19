package com.blog.writeapi.repositories;

import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.models.UserProfileModel;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<@NonNull UserProfileModel, @NonNull Long> {
    Boolean existsByUser(@IsModelInitialized UserModel user);

    Optional<UserProfileModel> findByUser(@IsModelInitialized UserModel user);
    
}
