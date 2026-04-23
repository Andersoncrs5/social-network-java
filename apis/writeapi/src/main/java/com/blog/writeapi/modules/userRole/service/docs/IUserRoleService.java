package com.blog.writeapi.modules.userRole.service.docs;

import com.blog.writeapi.modules.role.models.RoleModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.userRole.models.UserRoleModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.result.Result;

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
    boolean existsByUserIdAndRoleId(@IsId Long userId, @IsId Long roleId);
    Result<UserRoleModel> getByUserIdAndRoleId(@IsId Long userId, @IsId Long roleId);
    Result<Void> deleteByID(@IsId Long id);
}
