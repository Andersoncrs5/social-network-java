package com.blog.writeapi.unit;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.user.dtos.CreateUserDTO;
import com.blog.writeapi.modules.user.gateway.UserModuleGateway;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.user.repository.UserRepository;
import com.blog.writeapi.modules.user.service.providers.UserService;
import com.blog.writeapi.utils.mappers.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock private UserRepository repository;
    @Mock private Argon2PasswordEncoder encoder;
    @Mock private UserMapper mapper;
    @Mock private Snowflake snowflakeIdGenerator;
    @Mock private UserModuleGateway gateway;

    @InjectMocks private UserService service;

    private final Long FAKE_ID = 2000000000000000000L;
    private final String ENCRYPTED_PASSWORD = "abc";

    UserModel user = UserModel.builder()
            .id(1998780200074176609L)
            .name("user")
            .email("user@gmail.com")
            .password("12345678")
            .build();

    CreateUserDTO dto = new CreateUserDTO(
            this.user.getName(),
            this.user.getUsername(),
            this.user.getEmail(),
            this.user.getPassword()
    );

    @Test
    void shouldGetUserById() {
        when(repository.findById(user.getId())).thenReturn(Optional.of(user));

        Optional<UserModel> userModel = this.service.GetById(user.getId());

        assertThat(userModel.isPresent()).isTrue();

        assertThat(userModel.get().getId()).isEqualTo(user.getId());

        verify(repository, times(1)).findById(user.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnNullWhenGetUserById() {
        when(repository.findById(user.getId())).thenReturn(Optional.empty());

        Optional<UserModel> userModel = this.service.GetById(user.getId());

        assertThat(userModel.isEmpty()).isTrue();

        verify(repository, times(1)).findById(user.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnTrueWhenCheckIfExistsUserById() {
        when(repository.existsById(user.getId())).thenReturn(true);

        Boolean exists = this.service.ExistsById(user.getId());

        assertThat(exists).isTrue();

        verify(repository, times(1)).existsById(user.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnFalseWhenCheckIfExistsUserById() {
        when(repository.existsById(user.getId())).thenReturn(false);

        Boolean exists = this.service.ExistsById(user.getId());

        assertThat(exists).isFalse();

        verify(repository, times(1)).existsById(user.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldDeleteUser() {
        doNothing().when(repository).delete(user);

        this.service.Delete(user);

        verify(repository, times(1)).delete(user);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnUserWhenGetByEmail() {
        when(repository.findByEmailIgnoreCase(user.getEmail())).thenReturn(Optional.of(user));

        Optional<UserModel> optional = this.service.findByEmail(user.getEmail());

        assertThat(optional.isPresent()).isTrue();
        assertThat(optional.get().getId()).isEqualTo(user.getId());

        verify(this.repository, times(1)).findByEmailIgnoreCase(user.getEmail());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnNullWhenGetByEmail() {
        when(repository.findByEmailIgnoreCase(user.getEmail())).thenReturn(Optional.empty());

        Optional<UserModel> optional = this.service.findByEmail(user.getEmail());

        assertThat(optional.isEmpty()).isTrue();

        verify(this.repository, times(1)).findByEmailIgnoreCase(user.getEmail());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnTrueWhenExistsByUsername() {
        when(repository.existsByUsernameIgnoreCase(user.getUsername())).thenReturn(true);

        Boolean exists = this.service.existsByUsername(user.getUsername());

        assertThat(exists).isTrue();

        verify(repository, times(1)).existsByUsernameIgnoreCase(user.getUsername());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnFalseWhenExistsByUsername() {
        when(repository.existsByUsernameIgnoreCase(user.getUsername())).thenReturn(false);

        Boolean exists = this.service.existsByUsername(user.getUsername());

        assertThat(exists).isFalse();

        verify(repository, times(1)).existsByUsernameIgnoreCase(user.getUsername());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldCreateUser() {
        when(mapper.toModel(dto)).thenReturn(user);
        when(snowflakeIdGenerator.nextId()).thenReturn(user.getId());
        when(encoder.encode(dto.password())).thenReturn(user.getPassword());
        when(repository.save(any())).thenReturn(user);
        when(gateway.createUserSettings(user.getId())).thenReturn(any());

        this.service.Create(dto);

        verify(mapper, times(1)).toModel(dto);
        verify(snowflakeIdGenerator, times(1)).nextId();
        verify(encoder, times(1)).encode(dto.password());
        verify(repository, times(1)).save(any());
        verify(gateway, times(1)).createUserSettings(user.getId());

        InOrder order = inOrder(mapper, snowflakeIdGenerator, encoder, repository, gateway);

        order.verify(mapper).toModel(dto);
        order.verify(snowflakeIdGenerator).nextId();
        order.verify(encoder).encode(user.getPassword());
        order.verify(repository).save(user);
        order.verify(gateway).createUserSettings(user.getId());
    }

}
