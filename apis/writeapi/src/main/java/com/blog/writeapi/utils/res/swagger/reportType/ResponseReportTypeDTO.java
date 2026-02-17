package com.blog.writeapi.utils.res.swagger.reportType;

import com.blog.writeapi.modules.reportType.dto.ReportTypeDTO;
import com.blog.writeapi.utils.res.ResponseHttp;

public record ResponseReportTypeDTO(
        ResponseHttp<ReportTypeDTO> http
) {
}
