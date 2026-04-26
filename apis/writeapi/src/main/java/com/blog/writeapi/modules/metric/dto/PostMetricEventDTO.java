package com.blog.writeapi.modules.metric.dto;

import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.enums.metric.ActionEnum;
import com.blog.writeapi.utils.enums.metric.PostMetricEnum;
import jakarta.validation.constraints.NotNull;

public record PostMetricEventDTO(
        @IsId Long postId,
        @NotNull PostMetricEnum metric,
        @NotNull ActionEnum action
) {
    public static PostMetricEventDTO create(Long postId, PostMetricEnum metric, ActionEnum action) {
        return new PostMetricEventDTO(
                postId,
                metric,
                action
        );
    }
}