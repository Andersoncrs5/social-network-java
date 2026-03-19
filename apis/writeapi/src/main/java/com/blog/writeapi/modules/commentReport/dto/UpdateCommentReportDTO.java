package com.blog.writeapi.modules.commentReport.dto;

import com.blog.writeapi.utils.enums.report.ModerationActionType;
import com.blog.writeapi.utils.enums.report.ReportStatus;

public record UpdateCommentReportDTO(
        ReportStatus status,
        ModerationActionType moderationActionType,
        String moderatorNotes
) {
}
