package com.blog.writeapi.modules.reportType.dto;

import com.blog.writeapi.utils.enums.report.ReportPriority;

public record UpdateReportTypeDTO(
        String name,
        String description,
        String icon,
        String color,
        Integer displayOrder,
        ReportPriority defaultPriority,
        Boolean isActive,
        Boolean isVisible
) {
}
