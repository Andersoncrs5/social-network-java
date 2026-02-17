package com.blog.writeapi.modules.reportType.dto;

import com.blog.writeapi.modules.reportType.utils.validations.uniqueReportTypeByName.UniqueReportTypeByName;
import com.blog.writeapi.utils.enums.report.ReportPriority;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateReportTypeDTO(

        @UniqueReportTypeByName
        String name,

        @Size(max = 255)
        String description,

        @Size(max = 100)
        String icon,

        @Size(max = 20)
        String color,
        Integer displayOrder,

        @NotNull
        ReportPriority defaultPriority,
        Boolean isActive,
        Boolean isVisible
) {
}
