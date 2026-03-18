package com.blog.writeapi.modules.postReportType.dto;

import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;

public record CreatePostReportTypeDTO(
        @IsId Long reportId,
        @IsId Long typeId
) {
}
