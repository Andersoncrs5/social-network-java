package com.blog.writeapi.modules.commentReportType.service.interfaces;

import com.blog.writeapi.modules.commentReport.model.CommentReportModel;
import com.blog.writeapi.modules.commentReportType.model.CommentReportTypeModel;
import com.blog.writeapi.modules.reportType.model.ReportTypeModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.classes.ResultToggle;

import java.util.Optional;

public interface ICommentReportTypeService {
    Optional<CommentReportTypeModel> getById(@IsId Long id);
    void delete(@IsModelInitialized CommentReportTypeModel type);
    CommentReportTypeModel create(
            @IsModelInitialized CommentReportModel report,
            @IsModelInitialized ReportTypeModel type
    );
    Boolean existsByReportAndType(
            @IsModelInitialized CommentReportModel report,
            @IsModelInitialized ReportTypeModel type
    );
    ResultToggle<CommentReportTypeModel> toggle(
            @IsModelInitialized CommentReportModel report,
            @IsModelInitialized ReportTypeModel type,
            @IsId Long userID
    );
}
