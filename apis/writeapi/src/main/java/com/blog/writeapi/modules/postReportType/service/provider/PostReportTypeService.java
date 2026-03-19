package com.blog.writeapi.modules.postReportType.service.provider;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.postReportType.model.PostReportTypeModel;
import com.blog.writeapi.modules.postReportType.repository.PostReportTypeRepository;
import com.blog.writeapi.modules.postReportType.service.interfaces.IPostReportTypeService;
import com.blog.writeapi.modules.reportPost.model.PostReportModel;
import com.blog.writeapi.modules.reportType.model.ReportTypeModel;
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
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostReportTypeService implements IPostReportTypeService {

    private final PostReportTypeRepository repository;
    private final Snowflake generator;

    public Optional<PostReportTypeModel> getById(@IsId Long id) {
        return this.repository.findById(id);
    }

    public void delete(@IsModelInitialized PostReportTypeModel type) {
        this.repository.delete(type);
    }

    public PostReportTypeModel create(
            @IsModelInitialized PostReportModel report,
            @IsModelInitialized ReportTypeModel type
    ) {
        OffsetDateTime expirationLimit = report.getCreatedAt().plusMinutes(10);
        if (OffsetDateTime.now().isAfter(expirationLimit)) {
            throw new BusinessRuleException("The reporting window has expired. You can only add report types within 10 minutes of creation.");
        }

        PostReportTypeModel model = PostReportTypeModel.builder()
                .id(generator.nextId())
                .report(report)
                .type(type)
                .build();

        try {
            return this.repository.save(model);
        } catch (DataIntegrityViolationException e) {
            String message = e.getMostSpecificCause().getMessage();

            if (message != null && message.contains("uk_post_report_type")) {
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

    public Boolean existsByReportAndType(
            @IsModelInitialized PostReportModel report,
            @IsModelInitialized ReportTypeModel type
    ) {
        return this.repository.existsByReportAndType(report, type);
    }

    public ResultToggle<PostReportTypeModel> toggle(
            @IsModelInitialized PostReportModel report,
            @IsModelInitialized ReportTypeModel type,
            @IsId Long userID
    ) {
        if (!Objects.equals(report.getUser().getId(), userID)) {
            throw new BusinessRuleException("This report is not your");
        }

        OffsetDateTime expirationLimit = report.getCreatedAt().plusMinutes(10);
        if (OffsetDateTime.now().isAfter(expirationLimit)) {
            throw new BusinessRuleException("The reporting window has expired. You can only add report types within 10 minutes of creation.");
        }

        Optional<PostReportTypeModel> exists = this.repository.findByReportAndType(report, type);

        if (exists.isPresent()) {
            this.repository.delete(exists.get());
            return ResultToggle.removed();
        }

        PostReportTypeModel model = PostReportTypeModel.builder()
            .id(generator.nextId())
            .type(type)
            .report(report)
            .build();

        PostReportTypeModel saved = this.repository.save(model);

        return ResultToggle.added(saved);
    }

}
