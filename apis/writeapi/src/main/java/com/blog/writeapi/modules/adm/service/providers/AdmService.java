package com.blog.writeapi.modules.adm.service.providers;

import com.blog.writeapi.modules.adm.dto.ToggleRoleDTO;
import com.blog.writeapi.modules.adm.gateway.AdmModuleGateway;
import com.blog.writeapi.modules.adm.service.docs.IAdmService;
import com.blog.writeapi.modules.role.models.RoleModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.userRole.models.UserRoleModel;
import com.blog.writeapi.utils.classes.ResultToggle;
import com.blog.writeapi.utils.exceptions.BusinessRuleException;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class AdmService implements IAdmService {

    private final AdmModuleGateway gateway;


    public Result<UserRoleModel> addRoleToUser(
            ToggleRoleDTO dto
    ) {
        Optional<RoleModel> roleOpt = this.gateway.findRoleByName(dto.roleName());
        if (roleOpt.isEmpty()) return Result.notFound("Role not found");
        RoleModel role = roleOpt.get();

        String roleName = role.getName().toUpperCase();
        if (
                roleName.equals("SUPER_ADM_ROLE") ||
                roleName.equals("ADM_ROLE")
        ) return Result.forb("This role cannot used!");

        Optional<UserModel> userOpt = this.gateway.findUserById(dto.userId());
        if (userOpt.isEmpty()) return Result.notFound("User not found");
        UserModel user = userOpt.get();

        boolean exists = this.gateway.existsByUserIdAndRoleId(dto.userId(), role.getId());
        if (exists) return Result.conflict("User already has the role");

        UserRoleModel userRoleModel = this.gateway.addRoleToUser(user, role);

        return Result.created(userRoleModel);
    }

    public Result<Void> removeRoleToUser(
            ToggleRoleDTO dto
    ) {
        Optional<RoleModel> roleOpt = this.gateway.findRoleByName(dto.roleName());
        if (roleOpt.isEmpty()) return Result.notFound("Role not found");
        RoleModel role = roleOpt.get();

        String roleName = role.getName().toUpperCase();
        if (roleName.equals("SUPER_ADM_ROLE") || roleName.equals("ADM_ROLE"))
            return Result.forb("This role cannot used!");

        Optional<UserModel> userOpt = this.gateway.findUserById(dto.userId());
        if (userOpt.isEmpty()) return Result.notFound("User not found");
        UserModel user = userOpt.get();

        Result<UserRoleModel> result = this.gateway.findByUserIdAndRoleId(user.getId(), role.getId());
        if (result.isFailure()) return Result.failure(result.getStatus(), result.getError());

        Result<Void> resultDelete = this.gateway.deleteRoleFormUser(result.getValue());
        if (resultDelete.isFailure()) return Result.failure(result.getStatus(), result.getError());

        return Result.success();
    }

    public ResultToggle<UserRoleModel> toggleRoleAdmInUser(ToggleRoleDTO dto, Long currentUserId) {
        if (Objects.equals(dto.userId(), currentUserId)) {
            throw new BusinessRuleException("You cannot toggle your own administrative role.");
        }

        Optional<RoleModel> roleOpt = this.gateway.findRoleByName("ADM_ROLE");
        if (roleOpt.isEmpty()) throw new ModelNotFoundException("Role not found");

        Optional<UserModel> userOpt = this.gateway.findUserById(dto.userId());
        if (userOpt.isEmpty()) throw new ModelNotFoundException("User not found");

        UserModel user = userOpt.get();
        RoleModel role = roleOpt.get();

        Result<UserRoleModel> existing = this.gateway.findByUserIdAndRoleId(user.getId(), role.getId());

        if (existing.isSuccess()) {
            this.gateway.deleteRoleFormUser(existing.getValue());
            return ResultToggle.removed();
        } else {
            UserRoleModel newUserRole = this.gateway.addRoleToUser(user, role);
            return ResultToggle.added(newUserRole);
        }
    }

}
