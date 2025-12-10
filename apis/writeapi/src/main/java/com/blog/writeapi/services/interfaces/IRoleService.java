package com.blog.writeapi.services.interfaces;

import com.blog.writeapi.models.RoleModel;

import java.util.Optional;

public interface IRoleService {
    Optional<RoleModel> findByName(String name);
    Optional<RoleModel> findById(Long id);
    Boolean existsById(Long id);
    Boolean existsByName(String name);
    void delete(RoleModel role);
    RoleModel create(RoleModel role);
}
