package com.blog.writeapi.unit;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.models.RoleModel;
import com.blog.writeapi.repositories.RoleRepository;
import com.blog.writeapi.services.providers.RoleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {

    @Mock private RoleRepository repository;

    @Mock private Snowflake generator;

    @InjectMocks private RoleService service;

    RoleModel role = new RoleModel().toBuilder()
            .id(1998780200074176609L)
            .name("USER_ROLE")
            .isActive(true)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    @Test
    void shouldReturnRoleWhenFindRoleByName() {
        when(repository.findByName(role.getName())).thenReturn(Optional.of(role));

        Optional<RoleModel> name = this.service.findByName(role.getName());

        assertThat(name.isPresent()).isTrue();

        verify(repository, times(1)).findByName(role.getName());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnNullWhenFindRoleByName() {
        when(repository.findByName(role.getName())).thenReturn(Optional.empty());

        Optional<RoleModel> name = this.service.findByName(role.getName());

        assertThat(name.isEmpty()).isTrue();

        verify(repository, times(1)).findByName(role.getName());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnTrueWhenExistsRoleByName() {
        when(repository.existsByName(role.getName())).thenReturn(true);

        Boolean exists = this.service.existsByName(role.getName());

        assertThat(exists).isTrue();

        verify(repository, times(1)).existsByName(role.getName());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnFalseWhenExistsRoleByName() {
        when(repository.existsByName(role.getName())).thenReturn(false);

        Boolean exists = this.service.existsByName(role.getName());

        assertThat(exists).isFalse();

        verify(repository, times(1)).existsByName(role.getName());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnRoleWhenFindRoleById() {
        when(repository.findById(role.getId())).thenReturn(Optional.of(role));

        Optional<RoleModel> exists = this.service.findById(role.getId());

        assertThat(exists.isPresent()).isTrue();

        verify(repository, times(1)).findById(role.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnNullWhenFindRoleById() {
        when(repository.findById(role.getId())).thenReturn(Optional.empty());

        Optional<RoleModel> name = this.service.findById(role.getId());

        assertThat(name.isEmpty()).isTrue();

        verify(repository, times(1)).findById(role.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnTrueWhenExistsRoleById() {
        when(repository.existsById(role.getId())).thenReturn(true);

        Boolean exists = this.service.existsById(role.getId());

        assertThat(exists).isTrue();

        verify(repository, times(1)).existsById(role.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnFalseWhenExistsRoleById() {
        when(repository.existsById(role.getId())).thenReturn(false);

        Boolean exists = this.service.existsById(role.getId());

        assertThat(exists).isFalse();

        verify(repository, times(1)).existsById(role.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldDeleteUser() {
        doNothing().when(repository).delete(role);

        this.service.delete(role);

        verify(repository, times(1)).delete(role);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldCreateNewRole() {
        RoleModel roleCopy = new RoleModel().toBuilder()
                .name(role.getName())
                .isActive(role.getIsActive())
                .createdAt(role.getCreatedAt())
                .build();

        when(repository.save(roleCopy)).thenReturn(role);
        when(generator.nextId()).thenReturn(role.getId());

        RoleModel model = this.service.create(roleCopy);

        assertThat(model.getId()).isEqualTo(roleCopy.getId());
        assertThat(model.getName()).isEqualTo(roleCopy.getName());
        assertThat(model.getIsActive()).isEqualTo(roleCopy.getIsActive());
        assertThat(model.getCreatedAt()).isEqualTo(roleCopy.getCreatedAt());

        verify(repository, times(1)).save(roleCopy);
        verify(generator, times(1)).nextId();
        verifyNoMoreInteractions(repository);
        verifyNoMoreInteractions(generator);
    }
}