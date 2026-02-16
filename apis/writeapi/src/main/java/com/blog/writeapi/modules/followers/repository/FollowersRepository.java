package com.blog.writeapi.modules.followers.repository;

import com.blog.writeapi.modules.followers.models.FollowersModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowersRepository extends JpaRepository<FollowersModel, Long> {
    Optional<FollowersModel> findByFollowerAndFollowing(
            @IsModelInitialized UserModel follower,
            @IsModelInitialized UserModel following
    );
}
