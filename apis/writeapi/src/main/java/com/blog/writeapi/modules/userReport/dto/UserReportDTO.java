package com.blog.writeapi.modules.userReport.dto;

import com.blog.writeapi.modules.user.dtos.UserDTO;
import com.blog.writeapi.utils.enums.report.ModerationActionType;
import com.blog.writeapi.utils.enums.report.ReportReason;
import com.blog.writeapi.utils.enums.report.ReportStatus;

import java.time.OffsetDateTime;

public record UserReportDTO(
        Long id,
        String description,
        ReportStatus status,
        ModerationActionType moderationActionType,
        String moderatorNotes,
        ReportReason reason,
        OffsetDateTime resolvedAt,
        UserDTO reportedUser,
        UserDTO reporter,
        UserDTO moderator,
        String userProfileSnapshot,
        Double aiTrustScoreAtReport,
        Long version,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
