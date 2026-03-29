package com.blog.writeapi.modules.userReportType.dto;

import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;

public record CreateUserReportTypeDTO(
        @IsId Long reportId,
        @IsId Long typeId
) {
}
