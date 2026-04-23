package com.blog.writeapi.modules.adm.gateway;

import com.blog.writeapi.modules.role.models.RoleModel;
import com.blog.writeapi.modules.role.service.docs.IRoleService;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.user.service.docs.IUserService;
import com.blog.writeapi.modules.userRole.models.UserRoleModel;
import com.blog.writeapi.modules.userRole.service.docs.IUserRoleService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AdmModuleGateway {
    private final IRoleService roleService;
    private final IUserService userService;
    private final IUserRoleService userRoleService;

    public boolean existsByUserIdAndRoleId(@IsId Long userId, @IsId Long roleId){
        return userRoleService.existsByUserIdAndRoleId(userId, roleId);
    }

    public Result<UserRoleModel> findByUserIdAndRoleId(@IsId Long userId, @IsId Long roleId){
        return userRoleService.getByUserIdAndRoleId(userId, roleId);
    }

    public Optional<RoleModel> findRoleByName(String name) {
        return roleService.findByName(name);
    }

    public Optional<UserModel> findUserById(@IsId Long id) {
        return userService.GetById(id);
    }

    public UserRoleModel addRoleToUser(
            @IsModelInitialized UserModel user,
            @IsModelInitialized RoleModel role
    ) {
        return userRoleService.create(user, role);
    }

    public Result<Void> deleteRoleFormUser(UserRoleModel value) {
        return userRoleService.deleteByID(value.getId());
    }
}
