package com.blog.writeapi.modules.userReport.service.provider;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.metric.dto.UserMetricEventDTO;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.userReport.dto.CreateUserReportDTO;
import com.blog.writeapi.modules.userReport.dto.UpdateUserReportDTO;
import com.blog.writeapi.modules.userReport.gateway.UserReportModuleGateway;
import com.blog.writeapi.modules.userReport.model.UserReportModel;
import com.blog.writeapi.modules.userReport.repository.UserReportRepository;
import com.blog.writeapi.modules.userReport.service.docs.IUserReportService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.enums.metric.ActionEnum;
import com.blog.writeapi.utils.enums.metric.UserMetricEnum;
import com.blog.writeapi.utils.enums.report.ModerationActionType;
import com.blog.writeapi.utils.enums.report.ReportStatus;
import com.blog.writeapi.utils.exceptions.BusinessRuleException;
import com.blog.writeapi.utils.exceptions.InternalServerErrorException;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.exceptions.UniqueConstraintViolationException;
import com.blog.writeapi.utils.mappers.UserReportMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserReportService implements IUserReportService {

    private final UserReportRepository repository;
    private final UserReportModuleGateway gateway;
    private final Snowflake snowflake;
    private final UserReportMapper mapper;
    private final ObjectMapper objectMapper;

    public UserReportModel findByIdSimple(@IsId Long id) {
        return repository.findById(id).orElseThrow(
                () -> new ModelNotFoundException("User Report not found")
        );
    }

    public void delete(
            @IsModelInitialized UserReportModel report,
            @IsId Long userId
    ) {
        OffsetDateTime limit = report.getCreatedAt().plusHours(24);
        if (OffsetDateTime.now().isAfter(limit)) {
            throw new BusinessRuleException("Reports can only be deleted within 24 hours of creation.", HttpStatus.FORBIDDEN);
        }

        if (!Objects.equals(report.getReporter().getId(), userId)) {
            throw new BusinessRuleException("You have not permission to delete this report", HttpStatus.FORBIDDEN);
        }

        this.repository.delete(report);

        gateway.handleMetricUser(UserMetricEventDTO.create(
                report.getReportedUser().getId(),
                UserMetricEnum.REPORT_RECEIVED,
                ActionEnum.RED
        ));
    }

    public boolean existsByReportedUserIdAndReporterId(
            @IsId Long reportedUserId,
            @IsId Long reporterId
    ) {
        return repository.existsByReportedUserIdAndReporterId(reportedUserId, reporterId);
    }

    public UserReportModel create(
        CreateUserReportDTO dto,
        @IsId Long reportedUserId,
        @IsId Long reporterId
    ) {
        if (Objects.equals(reportedUserId, reporterId)) {
            throw new BusinessRuleException("You cannot report yourself");
        }

        UserModel reportedUser = this.gateway.findUserById(reportedUserId);
        UserModel reporter = this.gateway.findUserById(reporterId);

        UserReportModel report = this.mapper.toModel(dto);
        report.setReportedUser(reportedUser);
        report.setReporter(reporter);
        report.setId(snowflake.nextId());
        report.setModerationActionType(ModerationActionType.NONE);
        report.setStatus(ReportStatus.PENDING);

        try {
            report.setUserProfileSnapshot(objectMapper.writeValueAsString(reportedUser));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        try {
            UserReportModel save = repository.save(report);

            gateway.handleMetricUser(UserMetricEventDTO.create(
                    save.getReportedUser().getId(),
                    UserMetricEnum.REPORT_RECEIVED,
                    ActionEnum.SUM
            ));

            return save;
        } catch (DataIntegrityViolationException e) {
            String message = e.getMostSpecificCause().getMessage();

            if (message != null && message.contains("uk_user_report_reported_user_id_and_reporter_id")) {
                throw new UniqueConstraintViolationException(
                        "This report already has this type assigned."
                );
            }

            throw new BusinessRuleException("Database integrity error: " + message);
        } catch (Exception e) {
            log.error("Error creating PostReportType", e);
            throw new InternalServerErrorException("Error creating report association.");
        }
    }

    public UserReportModel update(
            UpdateUserReportDTO dto,
            @IsModelInitialized UserReportModel report,
            @IsId Long moderatorId
    ) {
        if (report.getModeratedAt() != null) {
            OffsetDateTime limit = report.getModeratedAt().plusDays(1);

            if (OffsetDateTime.now().isAfter(limit)) {
                throw new BusinessRuleException("The edit window for this report (24h) has expired.");
            }
        }

        UserModel moderator = this.gateway.findUserById(moderatorId);

        this.mapper.merge(dto, report);
        report.setModerator(moderator);

        if (report.getModeratedAt() == null)
            report.setModeratedAt(OffsetDateTime.now());

        return repository.save(report);
    }

}
