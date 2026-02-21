package com.blog.writeapi.modules.reportPost.dto;

import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.enums.report.ReportReason;

public record CreatePostReportDTO(
        String description,
        ReportReason reason,
        @IsId
        Long postId
) {
}
