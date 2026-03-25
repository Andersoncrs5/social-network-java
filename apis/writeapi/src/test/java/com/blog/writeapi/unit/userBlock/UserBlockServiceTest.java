package com.blog.writeapi.unit.userBlock;

import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.userBlock.gateway.UserBlockModuleGateway;
import com.blog.writeapi.modules.userBlock.model.UserBlockModel;
import com.blog.writeapi.modules.userBlock.repository.UserBlockRepository;
import com.blog.writeapi.modules.userBlock.service.provider.UserBlockService;
import com.blog.writeapi.utils.classes.ResultToggle;
import com.blog.writeapi.utils.exceptions.UniqueConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserBlockServiceTest {

    @Mock private UserBlockModuleGateway gateway;
    @Mock private UserBlockRepository repository;
    @InjectMocks private UserBlockService service;

    UserModel blocker = UserModel.builder()
            .id(1998780200074176609L)
            .name("user")
            .email("user@gmail.com")
            .password("12345678")
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    UserModel blocked = UserModel.builder()
            .id(1111111111174176609L)
            .name("user1")
            .email("user1@gmail.com")
            .password("12345678")
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    UserBlockModel block = UserBlockModel.builder()
            .id(1111111111122221119L)
            .blocked(blocked)
            .blocker(blocker)
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    @Test
    void shouldDelete() {
        doNothing()
                .when(repository)
                .delete(block);

        this.service.delete(block);

        verify(repository, times(1)).delete(block);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnBlockWhenFindByBlockerIdAndBlockedId() {
        when(repository.findByBlockerIdAndBlockedId(anyLong(), anyLong()))
                .thenReturn(Optional.of(block));

        Optional<UserBlockModel> optional = this.service.findByBlockerIdAndBlockedId(anyLong(), anyLong());

        assertThat(optional.isPresent()).isTrue();
        assertThat(optional.get().getId()).isEqualTo(block.getId());

        verify(repository, times(1))
                .findByBlockerIdAndBlockedId(anyLong(), anyLong());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnNullWhenFindByBlockerIdAndBlockedId() {
        when(repository.findByBlockerIdAndBlockedId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        Optional<UserBlockModel> optional = this.service.findByBlockerIdAndBlockedId(anyLong(), anyLong());

        assertThat(optional.isEmpty()).isTrue();

        verify(repository, times(1))
                .findByBlockerIdAndBlockedId(anyLong(), anyLong());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldCreate() {
        when(gateway.findByUserId(blocked.getId()))
                .thenReturn(blocked);
        when(gateway.findByUserId(blocker.getId()))
                .thenReturn(blocker);
        when(gateway.generateId())
                .thenReturn(block.getId());
        when(repository.save(any()))
                .thenReturn(block);

        UserBlockModel model = this.service.create(blocker.getId(), blocked.getId());

        assertThat(model.getId())
                .isEqualTo(block.getId());

        verify(gateway, times(1)).findByUserId(blocked.getId());
        verify(gateway, times(1)).findByUserId(blocker.getId());
        verify(gateway, times(1)).generateId();
        verify(repository, times(1)).save(any(UserBlockModel.class));

        InOrder order = inOrder(repository, gateway);

        order.verify(gateway).findByUserId(blocked.getId());
        order.verify(gateway).findByUserId(blocker.getId());
        order.verify(gateway).generateId();
        order.verify(repository).save(any());

        verifyNoMoreInteractions(repository, gateway);
    }

    @Test
    void shouldThrowUniqueConstraintViolationExceptionWhenCreate() {
        Long blockerId = 1L;
        Long blockedId = 2L;

        when(gateway.findByUserId(blockedId)).thenReturn(UserModel.builder().id(blockedId).build());
        when(gateway.findByUserId(blockerId)).thenReturn(UserModel.builder().id(blockerId).build());
        when(gateway.generateId()).thenReturn(100L);

        Throwable rootCause = new Throwable("Detail: Key (blocker_id, blocked_id)=(1, 2) already exists. idx_user_blocked_keys");

        DataIntegrityViolationException exception = mock(DataIntegrityViolationException.class);
        when(exception.getMostSpecificCause()).thenReturn(rootCause);

        when(repository.save(any(UserBlockModel.class))).thenThrow(exception);

        UniqueConstraintViolationException thrown = assertThrows(
                UniqueConstraintViolationException.class,
                () -> service.create(blockerId, blockedId)
        );

        assertThat(thrown.getMessage()).isEqualTo("This blocked already has this user");

        verify(repository, times(1)).save(any(UserBlockModel.class));
    }

}
