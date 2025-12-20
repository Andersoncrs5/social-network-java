package com.blog.writeapi.services.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.models.RoleModel;
import com.blog.writeapi.repositories.RoleRepository;
import com.blog.writeapi.services.interfaces.IRoleService;
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
        return this.repository.findByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RoleModel> findById(Long id) {
        return this.repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean existsById(Long id) {
        return this.repository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean existsByName(String name) {
        return this.repository.existsByName(name);
    }

    @Override
    @Transactional
    public void delete(RoleModel role) {
        this.repository.delete(role);
    }

    @Override
    @Transactional
    public RoleModel create(RoleModel role) {
        role.setId(snowflakeIdGenerator.nextId());
        role.setName(role.getName().toUpperCase());

        return this.repository.save(role);
    }

}
