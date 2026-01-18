package com.blog.writeapi.services.interfaces;

import com.blog.writeapi.models.RoleModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.models.UserRoleModel;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;

import java.util.List;
import java.util.Optional;

public interface IUserRoleService {
    UserRoleModel create(@IsModelInitialized UserModel user,@IsModelInitialized RoleModel role);
    List<UserRoleModel> getAllByUser(@IsModelInitialized UserModel user);
    void delete(@IsModelInitialized UserRoleModel user);
    Optional<UserRoleModel> getById(@IsId Long id);
    Boolean existsById(@IsId Long id);
    Optional<UserRoleModel> getByUserAndRole(@IsModelInitialized UserModel user, @IsModelInitialized RoleModel role);
    Boolean existsByUserAndRole(@IsModelInitialized UserModel user, @IsModelInitialized RoleModel role);
}
