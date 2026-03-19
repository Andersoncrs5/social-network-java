package com.blog.writeapi.modules.commentReportType.service.provider;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.commentReport.model.CommentReportModel;
import com.blog.writeapi.modules.commentReportType.model.CommentReportTypeModel;
import com.blog.writeapi.modules.commentReportType.repository.CommentReportTypeRepository;
import com.blog.writeapi.modules.commentReportType.service.interfaces.ICommentReportTypeService;
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
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentReportTypeService implements ICommentReportTypeService {

    private final CommentReportTypeRepository repository;
    private final Snowflake generator;

    @Override
    public Optional<CommentReportTypeModel> getById(@IsId Long id) {
        return this.repository.findById(id);
    }

    @Override
    public void delete(@IsModelInitialized CommentReportTypeModel type) {
        this.repository.delete(type);
    }

    @Override
    public CommentReportTypeModel create(
            @IsModelInitialized CommentReportModel report,
            @IsModelInitialized ReportTypeModel type
    ) {
        OffsetDateTime expirationLimit = report.getCreatedAt().plusMinutes(10);
        if (OffsetDateTime.now().isAfter(expirationLimit)) {
            throw new BusinessRuleException("The reporting window has expired. You can only add report types within 10 minutes of creation.");
        }

        CommentReportTypeModel model = CommentReportTypeModel.builder()
                .id(generator.nextId())
                .report(report)
                .type(type)
                .build();

        try {
            return this.repository.save(model);
        } catch (DataIntegrityViolationException e) {
            String message = e.getMostSpecificCause().getMessage();

            if (message != null && message.contains("uk_comment_report_type")) {
                throw new UniqueConstraintViolationException(
                        "This report already has this type assigned."
                );
            }

            throw new BusinessRuleException("Database integrity error: " + message);
        } catch (Exception e) {
            log.error("Error creating CommentReportType", e);
            throw new InternalServerErrorException("Error creating report association.");
        }
    }

    @Override
    public Boolean existsByReportAndType(
            @IsModelInitialized CommentReportModel report,
            @IsModelInitialized ReportTypeModel type
    ) {
        return this.repository.existsByReportAndType(report, type);
    }

    @Override
    public ResultToggle<CommentReportTypeModel> toggle(
            @IsModelInitialized CommentReportModel report,
            @IsModelInitialized ReportTypeModel type
    ) {
        Optional<CommentReportTypeModel> exists = this.repository.findByReportAndType(report, type);

        if (exists.isPresent()) {
            this.repository.delete(exists.get());
            return ResultToggle.removed();
        }

        CommentReportTypeModel model = CommentReportTypeModel.builder()
                .id(generator.nextId())
                .type(type)
                .report(report)
                .build();

        CommentReportTypeModel saved = this.repository.save(model);

        return ResultToggle.added(saved);
    }
}