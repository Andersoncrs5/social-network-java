package com.blog.writeapi.modules.reportType.model;

import com.blog.writeapi.utils.bases.models.BaseEntity;
import com.blog.writeapi.utils.enums.report.ReportPriority;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "report_types",
        indexes = {
                @Index(name = "idx_report_type_default_priority", columnList = "default_priority"),
                @Index(name = "idx_report_type_is_active", columnList = "is_active"),
        }
)
@Setter
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ReportTypeModel extends BaseEntity {

    @Column(unique = true, nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "default_priority")
    private ReportPriority defaultPriority;

    @Column(length = 100)
    private String icon;

    @Column(length = 20)
    private String color;

    private Integer displayOrder;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Builder.Default
    @Column(name = "is_visible", nullable = false)
    private Boolean isVisible = true;
}
