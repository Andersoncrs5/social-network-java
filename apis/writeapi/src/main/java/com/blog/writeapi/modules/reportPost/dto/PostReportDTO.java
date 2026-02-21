package com.blog.writeapi.modules.reportPost.dto;

import com.blog.writeapi.modules.post.dtos.PostDTO;
import com.blog.writeapi.modules.user.dtos.UserDTO;
import com.blog.writeapi.utils.enums.report.ModerationActionType;
import com.blog.writeapi.utils.enums.report.ReportReason;
import com.blog.writeapi.utils.enums.report.ReportStatus;

import java.time.OffsetDateTime;

public record PostReportDTO(
        Long id,
        String description,
        ReportStatus status,
        ModerationActionType moderationActionType,
        String moderatorNotes,
        ReportReason reason,
        OffsetDateTime resolvedAt,
        PostDTO post,
        UserDTO user,
        UserDTO moderator,
        Long postAuthorId,
        String postContentSnapshot,
        Double aiToxicityScore,
        Long version,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
