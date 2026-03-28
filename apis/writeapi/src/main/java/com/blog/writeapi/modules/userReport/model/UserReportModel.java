package com.blog.writeapi.modules.userReport.model;

import com.blog.writeapi.modules.user.models.UserModel;
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
        name = "user_reports",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_report", columnNames = {"reported_user_id", "reporter_id"})
        },
        indexes = {
                @Index(name = "idx_user_reports_reported_id", columnList = "reported_user_id"),
                @Index(name = "idx_user_reports_reporter_id", columnList = "reporter_id")
        }
)
@Setter
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserReportModel extends ReportBase {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "reported_user_id",
            nullable = false,
            columnDefinition = "BIGINT UNSIGNED"
    )
    private UserModel reportedUser; // O alvo da denúncia

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "reporter_id",
            nullable = false,
            columnDefinition = "BIGINT UNSIGNED"
    )
    private UserModel reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "moderator_id",
            columnDefinition = "BIGINT UNSIGNED"
    )
    private UserModel moderator;

    @Column(name = "moderated_at")
    private OffsetDateTime moderatedAt = null;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, name = "user_profile_snapshot")
    private String userProfileSnapshot;

    @Column(name = "ai_trust_score_at_report")
    private Double aiTrustScoreAtReport;

//    @JsonIgnore
//    @Builder.Default
//    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<UserReportTypeModel> reportTypes = new ArrayList<>();
}
