package com.blog.writeapi.modules.reportPost.model;

import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.postReportType.model.PostReportTypeModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.bases.models.ReportBase;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "post_reports",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_post_report", columnNames = {"post_id", "user_id"})
        },
        indexes = {
                @Index(name = "idx_post_reports_post_author_id", columnList = "post_author_id"),
        }
)
@Setter
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PostReportModel extends ReportBase {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "post_id",
            nullable = false,
            columnDefinition = "BIGINT UNSIGNED"
    )
    private PostModel post;

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

    @Column(name = "post_author_id")
    private Long postAuthorId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, name = "post_content_snapshot")
    private String postContentSnapshot;

    @Column(name = "ai_toxicity_score")
    private Double aiToxicityScore;

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostReportTypeModel> reportTypes = new ArrayList<>();
}
