package com.blog.writeapi.modules.metric.dto;

import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.enums.metric.ActionEnum;
import com.blog.writeapi.utils.enums.metric.CommentMetricEnum;
import jakarta.validation.constraints.NotNull;

public record CommentMetricEventDTO(
        @IsId Long commentId,
        @NotNull CommentMetricEnum metric,
        @NotNull ActionEnum action
) {
    public static CommentMetricEventDTO create(Long commentId, CommentMetricEnum metric, ActionEnum action) {
        return new CommentMetricEventDTO(
                commentId,
                metric,
                action
        );
    }
}
