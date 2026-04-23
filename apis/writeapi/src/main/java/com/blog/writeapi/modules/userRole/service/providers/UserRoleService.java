package com.blog.writeapi.modules.userRole.service.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.role.models.RoleModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.userRole.models.UserRoleModel;
import com.blog.writeapi.modules.userRole.repository.UserRoleRepository;
import com.blog.writeapi.modules.userRole.service.docs.IUserRoleService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserRoleService implements IUserRoleService {

    private final UserRoleRepository repository;
    private final Snowflake generator;

    @Override
    @Transactional
    public UserRoleModel create(@IsModelInitialized UserModel user, @IsModelInitialized RoleModel role) {
        UserRoleModel userRoleModel = new UserRoleModel().toBuilder()
                .id(generator.nextId())
                .user(user)
                .role(role)
                .build();

        return this.repository.save(userRoleModel);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserRoleModel> getById(@IsId Long id) {
        return this.repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean existsById(@IsId Long id) {
        return this.repository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserRoleModel> getByUserAndRole(@IsModelInitialized UserModel user, @IsModelInitialized RoleModel role) {
        return this.repository.findByUserAndRole(user, role);
    }

    @Override
    @Transactional(readOnly = true)
    public Result<UserRoleModel> getByUserIdAndRoleId(@IsId Long userId, @IsId Long roleId) {
        Optional<UserRoleModel> optional = this.repository.findByUserIdAndRoleId(userId, roleId);

        return optional.map(Result::success).orElseGet(() -> Result.notFound("User Role not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean existsByUserAndRole(@IsModelInitialized UserModel user, @IsModelInitialized RoleModel role) {
        return this.repository.existsByUserAndRole(user, role);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUserIdAndRoleId(@IsId Long userId, @IsId Long roleId) {
        return this.repository.existsByUserIdAndRoleId(userId, roleId);
    }

    @Override
    @Transactional
    public void delete(@IsModelInitialized UserRoleModel user){
        this.repository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserRoleModel> getAllByUser(@IsModelInitialized UserModel user){
        return this.repository.findAllByUser(user);
    }

    public Result<Void> deleteByID(@IsId Long id) {
        int result = this.repository.deleteByID(id);

        if (result == 0) return Result.notFound("Comment not found");

        return Result.success();
    }


}
