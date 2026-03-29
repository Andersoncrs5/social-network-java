package com.blog.writeapi.modules.userReportType.service.provider;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.reportType.model.ReportTypeModel;
import com.blog.writeapi.modules.userReport.model.UserReportModel;
import com.blog.writeapi.modules.userReportType.gateway.UserReportTypeModuleGateway;
import com.blog.writeapi.modules.userReportType.model.UserReportTypeModel;
import com.blog.writeapi.modules.userReportType.repository.UserReportTypeRepository;
import com.blog.writeapi.modules.userReportType.service.interfaces.IUserReportTypeService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.classes.ResultToggle;
import com.blog.writeapi.utils.exceptions.BusinessRuleException;
import com.blog.writeapi.utils.exceptions.InternalServerErrorException;
import com.blog.writeapi.utils.exceptions.UniqueConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserReportTypeService implements IUserReportTypeService {

    private final UserReportTypeRepository repository;
    private final UserReportTypeModuleGateway gateway;
    private final Snowflake generator;

    public boolean existsByReportIdAndTypeId(
            @IsId Long reportId,
            @IsId Long typeId
    ) {
        return repository.existsByReportIdAndTypeId(reportId, typeId);
    }

    public Optional<UserReportTypeModel> findByReportIdAndTypeId(
            @IsId Long reportId,
            @IsId Long typeId
    ) {
        return repository.findByReportIdAndTypeId(reportId, typeId);
    }

    public void delete(@IsModelInitialized UserReportTypeModel model) {
        this.repository.delete(model);
    }

    public UserReportTypeModel create(
            @IsId Long reportId,
            @IsId Long typeId,
            @IsId Long userId
    ) {
        UserReportModel userReport = this.gateway.findUserReportByIdSimple(reportId);

        OffsetDateTime expirationLimit = userReport.getCreatedAt().plusMinutes(10);
        if (OffsetDateTime.now().isAfter(expirationLimit)) {
            throw new BusinessRuleException("The reporting window has expired. You can only add report types within 10 minutes of creation.");
        }

        ReportTypeModel type = this.gateway.findReportTypeByIdSimple(typeId);

        UserReportTypeModel model = new UserReportTypeModel().toBuilder()
                .id(generator.nextId())
                .report(userReport)
                .type(type)
                .build();

        try {
             return this.repository.save(model);
        } catch (DataIntegrityViolationException e) {
            String message = e.getMostSpecificCause().getMessage();

            if (message != null && message.contains("uk_user_report_types")) {
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

    public ResultToggle<UserReportTypeModel> toggle(
            @IsId Long reportId,
            @IsId Long typeId,
            @IsId Long userId
    ) {
        var exists = this.repository.findByReportIdAndTypeId(reportId, typeId);

        if (exists.isPresent()) {
            this.delete(exists.get());

            return ResultToggle.removed();
        }

        UserReportTypeModel model = this.create(reportId, typeId, userId);

        return ResultToggle.added(model);
    }

}
