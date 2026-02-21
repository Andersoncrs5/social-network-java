package com.blog.writeapi.modules.reportPost.dto;

import com.blog.writeapi.utils.enums.report.ModerationActionType;
import com.blog.writeapi.utils.enums.report.ReportStatus;

public record UpdatePostReportDTO(
        ReportStatus status,
        ModerationActionType moderationActionType,
        String moderatorNotes
) {
}
