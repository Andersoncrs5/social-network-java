package com.blog.writeapi.unit.userView;

import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.userView.gateway.UserViewModuleGateway;
import com.blog.writeapi.modules.userView.model.UserViewModel;
import com.blog.writeapi.modules.userView.repository.UserViewRepository;
import com.blog.writeapi.modules.userView.service.provider.UserViewService;
import com.blog.writeapi.utils.enums.metric.ActionEnum;
import com.blog.writeapi.utils.enums.metric.UserMetricEnum;
import com.blog.writeapi.utils.result.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserViewServiceTest {

    @Mock private UserViewRepository repository;
    @Mock private UserViewModuleGateway gateway;
    @InjectMocks private UserViewService service;

    UserModel userViewed = new UserModel().toBuilder()
            .id(1998780111111111111L)
            .name("user-viewed")
            .email("user-viewed@gmail.com")
            .password("12345678")
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    UserModel userViewer = new UserModel().toBuilder()
            .id(199878000000000000L)
            .name("user-viewer")
            .email("user-viewer@gmail.com")
            .password("12345678")
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    UserViewModel view = new UserViewModel().toBuilder()
            .id(222222000000000000L)
            .viewer(userViewer)
            .viewed(userViewed)
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    @Test
    @DisplayName("Should create a user view successfully")
    void shouldCreateSuccessfully() {
        when(gateway.findUserById(userViewer.getId())).thenReturn(userViewer);
        when(gateway.findUserById(userViewed.getId())).thenReturn(userViewed);
        when(repository.save(any(UserViewModel.class))).thenReturn(view);

        Result<UserViewModel> result = service.create(userViewer.getId(), userViewed.getId());

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue()).isEqualTo(view);

        InOrder inOrder = inOrder(gateway, repository);
        inOrder.verify(gateway).findUserById(userViewer.getId());
        inOrder.verify(gateway).findUserById(userViewed.getId());
        inOrder.verify(repository).save(argThat(model ->
                model.getViewer().equals(userViewer) &&
                        model.getViewed().equals(userViewed)
        ));
        inOrder.verify(gateway).handleMetricUser(argThat(i ->
                i.action().equals(ActionEnum.SUM) &&
                        i.metric().equals(UserMetricEnum.USER_VIEW_RECEIVED) &&
                            i.userId().equals(userViewed.getId())
                ));

        verifyNoMoreInteractions(gateway, repository);
    }

    @Test
    @DisplayName("Should create a user view successfully")
    void shouldBadRequestUserViewYourself() {

        Result<UserViewModel> result = service.create(userViewer.getId(), userViewer.getId());

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getValue()).isNull();

        verify(repository, never()).save(any());
        verify(gateway, never()).findUserById(any());
        verify(gateway, never()).handleMetricUser(any());

        verifyNoMoreInteractions(gateway, repository);
    }

    @Test
    @DisplayName("Should return conflict when unique constraint uk_user_view is violated")
    void shouldReturnConflictOnDuplicateView() {
        when(gateway.findUserById(anyLong())).thenReturn(userViewer).thenReturn(userViewed);

        DataIntegrityViolationException exception = mock(DataIntegrityViolationException.class);
        Throwable cause = new Throwable("Error: uk_user_view violation");
        when(exception.getMostSpecificCause()).thenReturn(cause);

        when(repository.save(any())).thenThrow(exception);

        Result<UserViewModel> result = service.create(userViewer.getId(), userViewed.getId());

        assertThat(result.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(result.getError().message()).contains("already been added");

        verify(repository, times(1)).save(any());
    }

    @Test
    @DisplayName("Should return failure when an unexpected exception occurs")
    void shouldReturnInternalServerErrorOnGenericException() {
        when(gateway.findUserById(anyLong())).thenReturn(userViewer).thenReturn(userViewed);
        when(repository.save(any())).thenThrow(new RuntimeException("Crash!"));

        Result<UserViewModel> result = service.create(userViewer.getId(), userViewed.getId());

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        verify(repository).save(any());
    }

    @Test
    @DisplayName("Should return bad request on data integrity violation that is NOT unique constraint")
    void shouldReturnBadRequestOnOtherIntegrityErrors() {
        when(gateway.findUserById(anyLong())).thenReturn(userViewer);

        DataIntegrityViolationException exception = mock(DataIntegrityViolationException.class);
        when(exception.getMostSpecificCause()).thenReturn(new Throwable("fk_violation"));

        when(repository.save(any())).thenThrow(exception);

        Result<UserViewModel> result = service.create(userViewer.getId(), userViewed.getId());

        assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getMessage()).contains("Database integrity error");
    }

}
