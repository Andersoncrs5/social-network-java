package com.blog.writeapi.modules.followers.service.interfaces;

import com.blog.writeapi.modules.followers.dtos.UpdateFollowersDTO;
import com.blog.writeapi.modules.followers.models.FollowersModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;

import java.util.Optional;

public interface IFollowersService {
    FollowersModel getByIdSimple(@IsId Long id);
    Optional<FollowersModel> getByFollowerAndFollowing(
            @IsModelInitialized UserModel follower,
            @IsModelInitialized UserModel following
    );
    void delete(@IsModelInitialized FollowersModel follow);
    FollowersModel create(
            @IsModelInitialized UserModel follower,
            @IsModelInitialized UserModel following
    );
    FollowersModel update(
            @IsModelInitialized FollowersModel follow,
            UpdateFollowersDTO dto
    );
}
