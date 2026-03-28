package com.blog.writeapi.modules.userReport.dto;

import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.enums.report.ReportReason;

public record CreateUserReportDTO(
        String description,
        ReportReason reason,
        @IsId Long reportedUserId
) {
}
