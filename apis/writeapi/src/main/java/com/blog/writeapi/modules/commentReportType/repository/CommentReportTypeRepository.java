package com.blog.writeapi.modules.commentReportType.repository;

import com.blog.writeapi.modules.commentReport.model.CommentReportModel;
import com.blog.writeapi.modules.commentReportType.model.CommentReportTypeModel;
import com.blog.writeapi.modules.reportType.model.ReportTypeModel;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentReportTypeRepository extends JpaRepository<@NonNull CommentReportTypeModel, @NonNull Long> {
    Optional<CommentReportTypeModel> findByReportAndType(
            @IsModelInitialized CommentReportModel report,
            @IsModelInitialized ReportTypeModel type
    );

    Boolean existsByReportAndType(
            @IsModelInitialized CommentReportModel report,
            @IsModelInitialized ReportTypeModel type
    );
}
