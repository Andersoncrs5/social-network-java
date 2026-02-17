package com.blog.writeapi.utils.bases.models;

import com.blog.writeapi.utils.enums.report.ModerationActionType;
import com.blog.writeapi.utils.enums.report.ReportReason;
import com.blog.writeapi.utils.enums.report.ReportStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public abstract class ReportBase extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportReason reason;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private ReportStatus status = ReportStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private ModerationActionType moderationActionType;

    @Column(name = "moderator_notes", columnDefinition = "TEXT")
    private String moderatorNotes;

    @Column(name = "resolved_at")
    private OffsetDateTime resolvedAt;

}
