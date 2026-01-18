package com.blog.writeapi.repositories;

import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.models.UserProfileModel;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<@NonNull UserProfileModel, @NonNull Long> {
    Boolean existsByUser(UserModel user);

    Optional<UserProfileModel> findByUser(UserModel user);
    
}
