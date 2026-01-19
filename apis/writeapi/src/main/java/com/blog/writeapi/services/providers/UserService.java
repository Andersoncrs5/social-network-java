package com.blog.writeapi.services.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.dtos.user.CreateUserDTO;
import com.blog.writeapi.dtos.user.UpdateUserDTO;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.repositories.UserRepository;
import com.blog.writeapi.services.interfaces.IUserService;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository repository;
    private final UserMapper mapper;
    private final Snowflake snowflakeIdGenerator;
    private final Argon2PasswordEncoder encoder;

    @Override
    @Transactional(readOnly = true)
    public UserModel GetByIdSimple(@IsId Long id){
        return this.repository.findById(id).orElseThrow(
                () -> new ModelNotFoundException("User not found")
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean existsByUsername(String username) {
        return this.repository.existsByUsernameIgnoreCase(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserModel> GetById(@IsId Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean ExistsById(@IsId Long id) {
        return repository.existsById(id);
    }

    @Override
    @Transactional
    public void Delete(@IsModelInitialized UserModel user) {
        repository.delete(user);
    }

    @Override
    @Transactional
    public UserModel Update(UpdateUserDTO dto, @IsModelInitialized UserModel user) {
        mapper.merge(dto, user);

        if (dto.password() != null && !dto.password().isBlank())
            user.setPassword(encoder.encode(dto.password()));

        return this.repository.save(user);
    }

    @Override
    @Transactional
    public UserModel Create(CreateUserDTO dto) {
        UserModel user = mapper.toModel(dto);

        user.setId(snowflakeIdGenerator.nextId());
        user.setPassword(encoder.encode(user.getPassword()));

        return repository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserModel> findByEmail(String email) { return repository.findByEmailIgnoreCase(email); }

    @Override
    @Transactional
    public UserModel UpdateSimple(@IsModelInitialized UserModel user) { return this.repository.save(user); }

}
