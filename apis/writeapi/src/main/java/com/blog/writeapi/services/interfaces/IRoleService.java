package com.blog.writeapi.services.interfaces;

import com.blog.writeapi.models.RoleModel;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;

import java.util.Optional;

public interface IRoleService {
    Optional<RoleModel> findByName(String name);
    Optional<RoleModel> findById(@IsId Long id);
    Boolean existsById(@IsId Long id);
    Boolean existsByName(String name);
    void delete(@IsModelInitialized RoleModel role);
    RoleModel create(@IsModelInitialized RoleModel role);
}
