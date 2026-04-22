package com.blog.writeapi.modules.user.service.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.user.dtos.CreateUserDTO;
import com.blog.writeapi.modules.user.dtos.UpdateUserDTO;
import com.blog.writeapi.modules.user.gateway.UserModuleGateway;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.user.repository.UserRepository;
import com.blog.writeapi.modules.user.service.docs.IUserService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.exceptions.BusinessRuleException;
import com.blog.writeapi.utils.exceptions.InternalServerErrorException;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.exceptions.UniqueConstraintViolationException;
import com.blog.writeapi.utils.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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
    private final UserModuleGateway gateway;

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
    public void Delete(@IsModelInitialized UserModel user) {
        repository.delete(user);
    }

    @Override
    public UserModel Update(UpdateUserDTO dto, @IsModelInitialized UserModel user) {
        mapper.merge(dto, user);

        if (dto.password() != null && !dto.password().isBlank())
            user.setPassword(encoder.encode(dto.password()));

        return this.repository.save(user);
    }

    @Override
    public UserModel Create(CreateUserDTO dto) {
        UserModel user = mapper.toModel(dto);

        user.setId(snowflakeIdGenerator.nextId());
        user.setPassword(encoder.encode(user.getPassword()));

        UserModel userSaved = repository.save(user);
        this.gateway.createUserSettings(userSaved.getId());
        try {
            return userSaved;
        } catch (DataIntegrityViolationException e) {
            String message = Optional.ofNullable(e.getMostSpecificCause())
                    .map(Throwable::getMessage)
                    .orElse("").toLowerCase();

            if (message.contains("idx_username")) {
                throw new UniqueConstraintViolationException("The username is already in use.");
            }

            if (message.contains("idx_email")) {
                throw new UniqueConstraintViolationException("The email address is already registered.");
            }

            throw new BusinessRuleException("Error of integration in db: " + e.getMostSpecificCause().getMessage());
        } catch (Exception e) {
            throw new InternalServerErrorException("Erro ao processar criação de usuário.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserModel> findByEmail(String email) { return repository.findByEmailIgnoreCase(email); }

    @Override
    public UserModel UpdateSimple(@IsModelInitialized UserModel user) { return this.repository.save(user); }

}
