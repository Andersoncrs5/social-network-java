package com.blog.writeapi.unit;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.repositories.UserRepository;
import com.blog.writeapi.services.providers.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    @Mock private Snowflake snowflakeIdGenerator;

    @InjectMocks private UserService service;

    private final Long FAKE_ID = 2000000000000000000L;
    private final String ENCRYPTED_PASSWORD = "abc";

    UserModel user = UserModel.builder()
            .id(1998780200074176609L)
            .name("user")
            .email("user@gmail.com")
            .password("12345678")
            .build();

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

}
