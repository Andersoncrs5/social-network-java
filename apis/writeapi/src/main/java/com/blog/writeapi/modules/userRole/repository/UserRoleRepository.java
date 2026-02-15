package com.blog.writeapi.modules.userRole.repository;

import com.blog.writeapi.modules.role.models.RoleModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.userRole.models.UserRoleModel;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<@NonNull UserRoleModel, @NonNull Long> {
    Boolean existsByUserAndRole(@IsModelInitialized UserModel user, @IsModelInitialized RoleModel role);
    Optional<UserRoleModel> findByUserAndRole(@IsModelInitialized UserModel user, @IsModelInitialized RoleModel role);
    List<UserRoleModel> findAllByUser(@IsModelInitialized UserModel user);
}
