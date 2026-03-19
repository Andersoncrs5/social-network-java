package com.blog.writeapi.modules.commentReport.dto;

import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.enums.report.ReportReason;

public record CreateCommentReportDTO(
        String description,
        ReportReason reason,
        @IsId
        Long commentId
) {
}
