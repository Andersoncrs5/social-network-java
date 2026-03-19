package com.blog.writeapi.modules.commentReportType.model;

import com.blog.writeapi.modules.commentReport.model.CommentReportModel;
import com.blog.writeapi.modules.reportType.model.ReportTypeModel;
import com.blog.writeapi.utils.bases.models.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(
        name = "comment_report_types",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_comment_report_types", columnNames = {"report_id", "type_id"})
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class CommentReportTypeModel extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "report_id",
            nullable = false,
            columnDefinition = "BIGINT UNSIGNED"
    )
    private CommentReportModel report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "type_id",
            nullable = false,
            columnDefinition = "BIGINT UNSIGNED"
    )
    private ReportTypeModel type;

}
