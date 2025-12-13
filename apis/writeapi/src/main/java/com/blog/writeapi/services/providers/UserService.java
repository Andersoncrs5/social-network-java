package com.blog.writeapi.services.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.dtos.user.CreateUserDTO;
import com.blog.writeapi.dtos.user.UpdateUserDTO;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.repositories.UserRepository;
import com.blog.writeapi.services.interfaces.IUserService;
import com.blog.writeapi.utils.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

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
    public Optional<UserModel> GetById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Boolean ExistsById(Long id) {
        return repository.existsById(id);
    }

    @Override
    public void Delete(UserModel user) {
        repository.delete(user);
    }

    @Override
    public UserModel Update(UpdateUserDTO dto, UserModel user) {
        mapper.merge(dto, user);

        return this.repository.save(user);
    }

    @Override
    public UserModel Create(CreateUserDTO dto) {
        UserModel user = mapper.toModel(dto);

        user.setId(snowflakeIdGenerator.nextId());
        user.setPassword(encoder.encode(user.getPassword()));

        return repository.save(user);
    }

    @Override
    public Optional<UserModel> findByEmail(String email) { return repository.findByEmail(email); }

    @Override
    public UserModel UpdateSimple(UserModel user) { return this.repository.save(user); }

}
