package com.blog.writeapi.repositories;

import com.blog.writeapi.models.RoleModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.models.UserRoleModel;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;
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
