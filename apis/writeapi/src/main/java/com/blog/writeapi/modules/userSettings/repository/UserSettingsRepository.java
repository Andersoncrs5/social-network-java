package com.blog.writeapi.modules.userSettings.repository;

import com.blog.writeapi.modules.userSettings.model.UserSettingsModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSettingsRepository extends JpaRepository<UserSettingsModel, Long> {
    Optional<UserSettingsModel> findByUserId(@IsId Long userId);
}
