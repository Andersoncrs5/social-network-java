package com.blog.writeapi.services.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.dtos.user.CreateUserDTO;
import com.blog.writeapi.dtos.user.UpdateUserDTO;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.repositories.UserRepository;
import com.blog.writeapi.services.interfaces.IUserService;
import com.blog.writeapi.utils.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository repository;
    private final UserMapper mapper;
    private final Snowflake snowflakeIdGenerator;

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
        UserModel user = new UserModel();
        mapper.merge(dto, user);
        user.setId(snowflakeIdGenerator.nextId());

        return repository.save(user);
    }

}
