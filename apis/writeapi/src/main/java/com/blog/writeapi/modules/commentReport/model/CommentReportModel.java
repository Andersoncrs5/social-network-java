package com.blog.writeapi.modules.commentReport.model;

import com.blog.writeapi.modules.comment.models.CommentModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.bases.models.BaseEntity;
import com.blog.writeapi.utils.bases.models.ReportBase;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "comment_reports",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_comment_report", columnNames = {"comment_id", "user_id"})
        },
        indexes = {
                @Index(name = "idx_comment_reports_comment_author_id", columnList = "comment_author_id"),
                @Index(name = "idx_comment_reports_user_id", columnList = "user_id"),
        }
)
@Setter
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CommentReportModel extends ReportBase {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "comment_id",
            nullable = false,
            updatable = false
    )
    private CommentModel comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            columnDefinition = "BIGINT UNSIGNED"
    )
    private UserModel user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "moderator_id",
            columnDefinition = "BIGINT UNSIGNED"
    )
    private UserModel moderator;

    @Column(name = "moderated_at")
    private OffsetDateTime moderatedAt = null;

    @Column(name = "comment_author_id")
    private Long commentAuthorId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, name = "comment_content_snapshot", updatable = false)
    private String commentContentSnapshot;

    @Column(name = "ai_toxicity_score")
    private Double aiToxicityScore;
}
