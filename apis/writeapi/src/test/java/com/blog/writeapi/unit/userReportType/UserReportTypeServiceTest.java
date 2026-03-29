package com.blog.writeapi.unit.userReportType;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.reportType.model.ReportTypeModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.userReport.model.UserReportModel;
import com.blog.writeapi.modules.userReportType.gateway.UserReportTypeModuleGateway;
import com.blog.writeapi.modules.userReportType.model.UserReportTypeModel;
import com.blog.writeapi.modules.userReportType.repository.UserReportTypeRepository;
import com.blog.writeapi.modules.userReportType.service.provider.UserReportTypeService;
import com.blog.writeapi.utils.enums.report.ModerationActionType;
import com.blog.writeapi.utils.enums.report.ReportPriority;
import com.blog.writeapi.utils.enums.report.ReportStatus;
import com.blog.writeapi.utils.exceptions.UniqueConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserReportTypeServiceTest {

    @Mock private UserReportTypeRepository repository;
    @Mock private UserReportTypeModuleGateway gateway;
    @Mock private Snowflake generator;

    @InjectMocks private UserReportTypeService service;

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

    ReportTypeModel type = new ReportTypeModel().toBuilder()
            .id(1998780200074176609L)
            .name("report")
            .description("Desc")
            .defaultPriority(ReportPriority.LOW)
            .isActive(true)
            .isVisible(true)
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    UserReportTypeModel reportType = new UserReportTypeModel().toBuilder()
            .id(1998711111111111111L)
            .report(report)
            .type(type)
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    @Test
    void shouldReturnTrueWhenExistsByReportIdAndTypeId() {
        when(repository.existsByReportIdAndTypeId(reportType.getType().getId(), reportType.getReport().getId()))
                .thenReturn(true);

        boolean exists = this.service
                .existsByReportIdAndTypeId(reportType.getType().getId(), reportType.getReport().getId());

        assertThat(exists).isTrue();

        verify(repository, times(1)).existsByReportIdAndTypeId(reportType.getType().getId(), reportType.getReport().getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnFalseWhenExistsByReportIdAndTypeId() {
        when(repository.existsByReportIdAndTypeId(reportType.getType().getId(), reportType.getReport().getId()))
                .thenReturn(false);

        boolean exists = this.service
                .existsByReportIdAndTypeId(reportType.getType().getId(), reportType.getReport().getId());

        assertThat(exists).isFalse();

        verify(repository, times(1)).existsByReportIdAndTypeId(reportType.getType().getId(), reportType.getReport().getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldDelete() {
        doNothing().when(repository).delete(reportType);

        this.service.delete(reportType);

        verify(repository, times(1)).delete(reportType);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldCreateUserReportType() {
        // 1. Setup dos IDs para legibilidade
        Long reportId = report.getId();
        Long typeId = type.getId();
        Long reporterId = reporter.getId();

        // 2. Mocks - Seguindo EXATAMENTE a ordem das linhas no teu service.create
        // Linha 1 do Service: UserReportModel userReport = gateway.findUserReportByIdSimple(reportId);
        when(gateway.findUserReportByIdSimple(reportId)).thenReturn(report);

        // Linha 2 do Service: if (Objects.equals(userReport.getReporter().getId(), userId)) ...
        // (O Mockito já usará o objeto 'report' acima para essa validação)

        // Linha 3 do Service: ReportTypeModel type = gateway.findReportTypeByIdSimple(typeId);
        when(gateway.findReportTypeByIdSimple(typeId)).thenReturn(type);

        // Linha 4 do Service: id(generator.nextId())
        when(generator.nextId()).thenReturn(reportType.getId());

        // Linha 5 do Service: repository.save(model)
        when(repository.save(any(UserReportTypeModel.class))).thenReturn(reportType);

        // 3. Execução
        UserReportTypeModel model = service.create(reportId, typeId, reporterId);

        // 4. Asserts (Usando o que veio do teu Builder)
        assertThat(model).isNotNull();
        assertThat(model.getId()).isEqualTo(reportType.getId());
        assertThat(model.getReport().getDescription()).isEqualTo("any desc");
        assertThat(model.getType().getName()).isEqualTo("report");

        // 5. Verificações de chamada e InOrder (Fluxo Real)
        InOrder order = inOrder(gateway, generator, repository);

        order.verify(gateway).findUserReportByIdSimple(reportId);
        order.verify(gateway).findReportTypeByIdSimple(typeId);
        order.verify(generator).nextId();
        order.verify(repository).save(any());

        verifyNoMoreInteractions(gateway, repository, generator);
    }

    @Test
    void shouldThrowUniqueConstraintViolationExceptionWhenTypeAlreadyAssigned() {
        when(gateway.findReportTypeByIdSimple(anyLong())).thenReturn(type);
        when(gateway.findUserReportByIdSimple(anyLong())).thenReturn(this.report);
        when(generator.nextId()).thenReturn(1L);

        String dbErrorMessage = "Duplicate entry for key 'uk_user_report_types'";
        DataIntegrityViolationException exception = new DataIntegrityViolationException(
                "Conflict",
                new RuntimeException(dbErrorMessage) // MostSpecificCause
        );

        when(repository.save(any(UserReportTypeModel.class))).thenThrow(exception);

        UniqueConstraintViolationException thrown = assertThrows(
                UniqueConstraintViolationException.class,
                () -> service.create(1L, 1L, 1L)
        );

        assertThat(thrown.getMessage()).isEqualTo("This report already has this type assigned.");

        verify(repository).save(any());
    }



}
