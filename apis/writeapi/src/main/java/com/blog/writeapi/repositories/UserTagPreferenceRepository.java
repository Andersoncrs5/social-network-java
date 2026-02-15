package com.blog.writeapi.repositories;

import com.blog.writeapi.models.*;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserTagPreferenceRepository extends JpaRepository<UserTagPreferenceModel, Long> {
    Optional<UserTagPreferenceModel> findByUserAndTag(
            @IsModelInitialized UserModel user,
            @IsModelInitialized TagModel tag
    );
}
