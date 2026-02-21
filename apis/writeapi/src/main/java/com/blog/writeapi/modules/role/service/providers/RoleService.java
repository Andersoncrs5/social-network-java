package com.blog.writeapi.modules.role.service.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.role.models.RoleModel;
import com.blog.writeapi.modules.role.repository.RoleRepository;
import com.blog.writeapi.modules.role.service.docs.IRoleService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService implements IRoleService {

    private final RoleRepository repository;
    private final Snowflake snowflakeIdGenerator;

    @Override
    @Transactional(readOnly = true)
    public Optional<RoleModel> findByName(String name) {
        return this.repository.findByNameIgnoreCase(name);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleModel findByNameSimple(@NotBlank String name) {
        return this.repository.findByNameIgnoreCase(name).orElseThrow(() ->
                new ModelNotFoundException("Role not found with name: " + name)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RoleModel> findById(@IsId Long id) {
        return this.repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleModel findByIdSimple(@IsId Long id) {
        return this.repository.findById(id).orElseThrow(() ->
                new ModelNotFoundException("Role not found")
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean existsById(@IsId Long id) {
        return this.repository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean existsByName(String name) {
        return this.repository.existsByNameIgnoreCase(name);
    }

    @Override
    @Transactional
    public void delete(@IsModelInitialized RoleModel role) {
        this.repository.delete(role);
    }

    @Override
    @Transactional
    public RoleModel create(@IsModelInitialized RoleModel role) {
        role.setId(snowflakeIdGenerator.nextId());
        role.setName(role.getName().toUpperCase());

        return this.repository.save(role);
    }

}
