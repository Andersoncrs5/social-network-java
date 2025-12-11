package com.blog.writeapi.unit;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.models.RoleModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.models.UserRoleModel;
import com.blog.writeapi.repositories.UserRoleRepository;
import com.blog.writeapi.services.providers.UserRoleService;
import com.blog.writeapi.services.providers.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserRoleServiceTest {

    @Mock private UserRoleRepository repository;
    @Mock private Snowflake generator;
    @InjectMocks private UserRoleService service;

    UserModel user = UserModel.builder()
            .id(1998780200074176109L)
            .name("user")
            .email("user@gmail.com")
            .password("12345678")
            .build();

    RoleModel role = new RoleModel().toBuilder()
            .id(1998780200074136609L)
            .name("USER")
            .createdAt(LocalDateTime.now())
            .build();

    UserRoleModel userRole = new UserRoleModel().toBuilder()
            .id(1998780200074676609L)
            .role(role)
            .user(user)
            .createdAt(LocalDateTime.now())
            .build();

    @Test
    void shouldAddedNewRoleToUser(){
        when(repository.save(any(UserRoleModel.class))).thenReturn(userRole);
        when(generator.nextId()).thenReturn(userRole.getId());

        UserRoleModel roleModel = this.service.create(user, role);

        assertThat(roleModel.getId()).isEqualTo(userRole.getId());
        assertThat(roleModel.getUser().getId()).isEqualTo(userRole.getUser().getId());
        assertThat(roleModel.getRole().getId()).isEqualTo(userRole.getRole().getId());

        verify(repository, times(1)).save(any(UserRoleModel.class));
        verify(generator, times(1)).nextId();

        verifyNoMoreInteractions(repository);
        verifyNoMoreInteractions(generator);
    }

    @Test
    void shouldReturnUserRoleWhenFindById() {
        when(repository.findById(userRole.getId())).thenReturn(Optional.of(userRole));

        Optional<UserRoleModel> optional = this.service.getById(userRole.getId());

        assertThat(optional.isPresent()).isTrue();
        assertThat(optional.get().getId()).isEqualTo(userRole.getId());

        verify(repository, times(1)).findById(userRole.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnNullWhenFindById() {
        when(repository.findById(userRole.getId())).thenReturn(Optional.empty());

        Optional<UserRoleModel> optional = this.service.getById(userRole.getId());

        assertThat(optional.isEmpty()).isTrue();

        verify(repository, times(1)).findById(userRole.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnTrueWhenExistsById() {
        when(repository.existsById(userRole.getId())).thenReturn(true);

        Boolean exists = this.service.existsById(userRole.getId());

        assertThat(exists).isTrue();

        verify(repository, times(1)).existsById(userRole.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnFalseWhenExistsById() {
        when(repository.existsById(userRole.getId())).thenReturn(false);

        Boolean exists = this.service.existsById(userRole.getId());

        assertThat(exists).isFalse();

        verify(repository, times(1)).existsById(userRole.getId());
        verifyNoMoreInteractions(repository);
    }


}
