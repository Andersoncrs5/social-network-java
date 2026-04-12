package com.blog.writeapi.modules.postShare.repository;

import com.blog.writeapi.modules.postShare.model.PostShareModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.enums.postShare.SharePlatformEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostShareRepository extends JpaRepository<PostShareModel, Long> {
    Optional<PostShareModel> findByUserIdAndPostId(Long l, Long l1);

    Optional<PostShareModel> findByUserIdAndPostIdAndPlatform(
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
