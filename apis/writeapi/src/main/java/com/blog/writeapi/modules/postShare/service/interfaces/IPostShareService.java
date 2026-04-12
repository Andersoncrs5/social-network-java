package com.blog.writeapi.modules.postShare.service.interfaces;

import com.blog.writeapi.modules.postShare.model.PostShareModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.enums.postShare.SharePlatformEnum;

import java.util.Optional;

public interface IPostShareService {
    PostShareModel create(
            @IsId Long userId,
            @IsId Long postId,
            SharePlatformEnum platform
    );
    Optional<PostShareModel> findByUserIdAndPostId(
            @IsId Long userId,
            @IsId Long postId
    );
    Optional<PostShareModel> findByUserIdAndPostIdAndPlatform(
            @IsId Long userId,
            @IsId Long postId,
            SharePlatformEnum platform
    );
    void createIfNotExists(
            @IsId Long userId,
            @IsId Long postId,
            SharePlatformEnum platform
    );
    boolean existsByUserIdAndPostIdAndPlatform(
            @IsId Long userId,
            @IsId Long postId,
            SharePlatformEnum platform
    );
}
