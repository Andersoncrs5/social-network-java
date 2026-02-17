package com.blog.writeapi.modules.reportType.dto;

import com.blog.writeapi.utils.enums.report.ReportPriority;

import java.time.OffsetDateTime;

public record ReportTypeDTO(
        Long id,
        String name,
        String description,
        String icon,
        String color,
        Integer displayOrder,
        ReportPriority defaultPriority,
        Boolean isActive,
        Boolean isVisible,
        Long version,
        OffsetDateTime lastUsedAt,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
