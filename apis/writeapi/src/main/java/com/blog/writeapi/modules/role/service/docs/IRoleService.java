package com.blog.writeapi.modules.role.service.docs;

import com.blog.writeapi.modules.role.models.RoleModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;

import java.util.Optional;

public interface IRoleService {
    Optional<RoleModel> findByName(String name);
    Optional<RoleModel> findById(@IsId Long id);
    Boolean existsById(@IsId Long id);
    Boolean existsByName(String name);
    void delete(@IsModelInitialized RoleModel role);
    RoleModel create(@IsModelInitialized RoleModel role);
}
