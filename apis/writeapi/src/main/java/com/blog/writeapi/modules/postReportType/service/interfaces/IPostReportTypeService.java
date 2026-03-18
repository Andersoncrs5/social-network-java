package com.blog.writeapi.modules.postReportType.service.interfaces;

import com.blog.writeapi.modules.postReportType.model.PostReportTypeModel;
import com.blog.writeapi.modules.reportPost.model.PostReportModel;
import com.blog.writeapi.modules.reportType.model.ReportTypeModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.classes.ResultToggle;

import java.util.Optional;

public interface IPostReportTypeService  {
    Optional<PostReportTypeModel> getById(@IsId Long id);
    void delete(@IsModelInitialized PostReportTypeModel type);
    PostReportTypeModel create(
            @IsModelInitialized PostReportModel report,
            @IsModelInitialized ReportTypeModel type
    );
    Boolean existsByReportAndType(
            @IsModelInitialized PostReportModel report,
            @IsModelInitialized ReportTypeModel type
    );
    ResultToggle<PostReportTypeModel> toggle(
            @IsModelInitialized PostReportModel report,
            @IsModelInitialized ReportTypeModel type
    );
}
