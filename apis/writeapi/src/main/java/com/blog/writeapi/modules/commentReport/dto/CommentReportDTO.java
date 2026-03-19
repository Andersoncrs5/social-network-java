package com.blog.writeapi.modules.commentReport.dto;

import com.blog.writeapi.modules.comment.dtos.CommentDTO;
import com.blog.writeapi.modules.post.dtos.PostDTO;
import com.blog.writeapi.modules.user.dtos.UserDTO;
import com.blog.writeapi.utils.enums.report.ModerationActionType;
import com.blog.writeapi.utils.enums.report.ReportReason;
import com.blog.writeapi.utils.enums.report.ReportStatus;

import java.time.OffsetDateTime;

public record CommentReportDTO(
        Long id,
        String description,
        ReportStatus status,
        ModerationActionType moderationActionType,
        String moderatorNotes,
        ReportReason reason,
        OffsetDateTime resolvedAt,
        CommentDTO comment,
        UserDTO user,
        UserDTO moderator,
        Long commentAuthorId,
        String commentContentSnapshot,
        Double aiToxicityScore,
        Long version,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
