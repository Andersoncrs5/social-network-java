package com.blog.writeapi.modules.commentReportType.dto;

import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;

public record CreateCommentReportTypeDTO(
        @IsId Long reportId,
        @IsId Long typeId
) {
}
