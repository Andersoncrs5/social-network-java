package com.blog.writeapi.unit.userReport;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.userReport.dto.CreateUserReportDTO;
import com.blog.writeapi.modules.userReport.dto.UpdateUserReportDTO;
import com.blog.writeapi.modules.userReport.gateway.UserReportModuleGateway;
import com.blog.writeapi.modules.userReport.model.UserReportModel;
import com.blog.writeapi.modules.userReport.repository.UserReportRepository;
import com.blog.writeapi.modules.userReport.service.provider.UserReportService;
import com.blog.writeapi.utils.enums.metric.ActionEnum;
import com.blog.writeapi.utils.enums.metric.UserMetricEnum;
import com.blog.writeapi.utils.enums.report.ModerationActionType;
import com.blog.writeapi.utils.enums.report.ReportReason;
import com.blog.writeapi.utils.enums.report.ReportStatus;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.exceptions.UniqueConstraintViolationException;
import com.blog.writeapi.utils.mappers.UserReportMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserReportServiceTest {

    @Mock private UserReportRepository repository;
    @Mock private UserReportModuleGateway gateway;
    @Mock private UserReportMapper mapper;
    @Mock private Snowflake snowflake;
    @Mock private ObjectMapper objectMapper;

    @InjectMocks private UserReportService service;

    UserModel reportedUser = UserModel.builder()
            .id(1998780777777777778L)
            .name("user")
            .email("reportedUser@gmail.com")
            .password("12345678")
            .build();

    UserModel reporter = UserModel.builder()
            .id(1118111111174176609L)
            .name("reporter")
            .email("reporter@gmail.com")
            .password("12345678")
            .build();

    UserModel moderator = UserModel.builder()
            .id(1991111111174176609L)
            .name("moderator")
            .email("moderator@gmail.com")
            .password("12345678")
            .build();

    UserReportModel report = new UserReportModel().toBuilder()
            .id(1998710221111111109L)
            .description("any desc")
            .reportedUser(reportedUser)
            .reporter(reporter)
            .moderator(moderator)
            .userProfileSnapshot("postJson")
            .status(ReportStatus.RESOLVED)
            .moderationActionType(ModerationActionType.SHADOW_BANNED)
            .aiTrustScoreAtReport(2.1)
            .moderatorNotes("AnyNotes")
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    CreateUserReportDTO dto = new CreateUserReportDTO(
            "Desc",
            ReportReason.SPAM,
            reportedUser.getId()
    );

    UpdateUserReportDTO updateDTO = new UpdateUserReportDTO(
            report.getStatus(),
            report.getModerationActionType(),
            report.getModeratorNotes()
    );

    @Test
    void shouldReturnReportWhenFindById() {
        Mockito.when(repository.findById(this.report.getId()))
                .thenReturn(Optional.of(report));

        UserReportModel model = this.service.findByIdSimple(report.getId());

        assertThat(model.getId()).isEqualTo(report.getId());

        Mockito.verify(repository, Mockito.times(1)).findById(anyLong());
        Mockito.verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldThrowModelNotFoundExceptionWhenFindById() {
        Long nonExistentId = 1L;
        when(repository.findById(nonExistentId))
                .thenReturn(Optional.empty());

        ModelNotFoundException exception = assertThrows(ModelNotFoundException.class, () ->
                this.service.findByIdSimple(nonExistentId)
        );

        assertTrue(exception.getMessage().equalsIgnoreCase("User Report not found"));

        verify(repository, times(1)).findById(nonExistentId);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldDelete() {
        doNothing().when(repository).delete(report);

        this.service.delete(report, report.getReporter().getId());

        verify(repository, times(1)).delete(report);
        verify(gateway, times(1)).handleMetricUser(argThat(i ->
                i.userId().equals(report.getReportedUser().getId()) &&
                    i.metric().equals(UserMetricEnum.REPORT_RECEIVED) &&
                        i.action().equals(ActionEnum.RED)
                ));
    }

    @Test
    void shouldReturnTrueWhenExistsByReportedUserIdAndReporterId() {
        when(repository.existsByReportedUserIdAndReporterId(anyLong(), anyLong()))
                .thenReturn(true);

        boolean exists = service.existsByReportedUserIdAndReporterId(anyLong(), anyLong());

        assertThat(exists).isTrue();

        verify(repository, times(1)).existsByReportedUserIdAndReporterId(anyLong(), anyLong());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnFalseWhenExistsByReportedUserIdAndReporterId() {
        when(repository.existsByReportedUserIdAndReporterId(anyLong(), anyLong()))
                .thenReturn(false);

        boolean exists = service.existsByReportedUserIdAndReporterId(anyLong(), anyLong());

        assertThat(exists).isFalse();

        verify(repository, times(1)).existsByReportedUserIdAndReporterId(anyLong(), anyLong());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldCreateReport() throws JsonProcessingException {
        when(gateway.findUserById(reportedUser.getId()))
                .thenReturn(reportedUser);
        when(gateway.findUserById(reporter.getId()))
                .thenReturn(reporter);
        when(mapper.toModel(dto))
                .thenReturn(report);
        when(snowflake.nextId())
                .thenReturn(report.getId());
        when(objectMapper.writeValueAsString(any(UserModel.class)))
                .thenReturn(report.getUserProfileSnapshot());
        when(repository.save(any(UserReportModel.class)))
                .thenReturn(report);
        doNothing().when(gateway).handleMetricUser(any());

        UserReportModel model = service.create(dto, reportedUser.getId(), reporter.getId());

        assertThat(model.getId()).isEqualTo(report.getId());

        verify(gateway, times(1)).findUserById(reportedUser.getId());
        verify(gateway, times(1)).findUserById(reporter.getId());
        verify(mapper, times(1)).toModel(dto);
        verify(snowflake, times(1)).nextId();
        verify(objectMapper, times(1)).writeValueAsString(reportedUser);
        verify(repository, times(1)).save(any());
        verify(gateway, times(1)).handleMetricUser(argThat(i ->
                i.userId().equals(report.getReportedUser().getId()) &&
                        i.metric().equals(UserMetricEnum.REPORT_RECEIVED) &&
                        i.action().equals(ActionEnum.SUM)
        ));

        InOrder order = inOrder(gateway, mapper, snowflake, objectMapper, repository);

        order.verify(gateway).findUserById(reportedUser.getId());
        order.verify(gateway).findUserById(reporter.getId());
        order.verify(mapper).toModel(dto);
        order.verify(snowflake).nextId();
        order.verify(objectMapper).writeValueAsString(reportedUser);
        order.verify(repository).save(any());
        order.verify(gateway).handleMetricUser(any());

    }

    @Test
    void shouldThrowUniqueConstraintViolationExceptionWhenCreateUserReport() throws Exception {
        String constraintMessage = "uk_user_report_reported_user_id_and_reporter_id";
        DataIntegrityViolationException dbException = new DataIntegrityViolationException(
                "Duplicate entry",
                new RuntimeException(constraintMessage)
        );

        when(gateway.findUserById(reportedUser.getId()))
                .thenReturn(reportedUser);
        when(gateway.findUserById(reporter.getId()))
                .thenReturn(reporter);
        when(mapper.toModel(dto))
                .thenReturn(report);
        when(snowflake.nextId())
                .thenReturn(report.getId());
        when(objectMapper.writeValueAsString(any(UserModel.class)))
                .thenReturn(report.getUserProfileSnapshot());
        when(repository.save(any(UserReportModel.class)))
                .thenThrow(dbException);

        assertThrows(UniqueConstraintViolationException.class,
                () -> service.create(dto, reportedUser.getId(), reporter.getId())
        );

        verify(gateway, times(1)).findUserById(reportedUser.getId());
        verify(gateway, times(1)).findUserById(reporter.getId());
        verify(mapper, times(1)).toModel(dto);
        verify(snowflake, times(1)).nextId();
        verify(objectMapper, times(1)).writeValueAsString(reportedUser);
        verify(repository, times(1)).save(any());

        InOrder order = inOrder(gateway, mapper, snowflake, objectMapper, repository);

        order.verify(gateway).findUserById(reportedUser.getId());
        order.verify(gateway).findUserById(reporter.getId());
        order.verify(mapper).toModel(dto);
        order.verify(snowflake).nextId();
        order.verify(objectMapper).writeValueAsString(reportedUser);
        order.verify(repository).save(any());
    }

    @Test
    void shouldUpdateReport() {
        when(gateway.findUserById(moderator.getId()))
                .thenReturn(moderator);
        doNothing().when(mapper).merge(updateDTO, report);
        when(repository.save(any())).thenReturn(report);

        UserReportModel update = this.service.update(updateDTO, report, moderator.getId());

        assertThat(update.getId()).isEqualTo(report.getId());
        assertThat(update.getStatus()).isEqualTo(report.getStatus());
        assertThat(update.getModerationActionType()).isEqualTo(report.getModerationActionType());
        assertThat(update.getModeratorNotes()).isEqualTo(report.getModeratorNotes());

    }

}
