package com.blog.writeapi.modules.userReport.dto;

import com.blog.writeapi.utils.enums.report.ModerationActionType;
import com.blog.writeapi.utils.enums.report.ReportStatus;

public record UpdateUserReportDTO(
        ReportStatus status,
        ModerationActionType moderationActionType,
        String moderatorNotes
) {
}
