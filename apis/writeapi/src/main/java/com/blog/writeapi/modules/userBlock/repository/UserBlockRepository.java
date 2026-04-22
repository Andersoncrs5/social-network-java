package com.blog.writeapi.modules.userBlock.repository;

import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.userBlock.model.UserBlockModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserBlockRepository extends JpaRepository<UserBlockModel, Long> {
    Optional<UserBlockModel> findByBlockerAndBlocked(
            @IsModelInitialized UserModel blocker,
            @IsModelInitialized UserModel blocked
    );
    Optional<UserBlockModel> findByBlockerIdAndBlockedId(
            @IsId Long blockerId,
            @IsId Long blockedId
    );
    boolean existsByBlockerIdAndBlockedId(
            @IsId Long blockerId,
            @IsId Long blockedId
    );
}
