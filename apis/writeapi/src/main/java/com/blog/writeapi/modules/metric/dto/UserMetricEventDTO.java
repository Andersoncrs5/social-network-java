package com.blog.writeapi.modules.metric.dto;

import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.enums.metric.ActionEnum;
import com.blog.writeapi.utils.enums.metric.UserMetricEnum;
import jakarta.validation.constraints.NotNull;

public record UserMetricEventDTO(
        @IsId Long userId,
        @NotNull UserMetricEnum metric,
        @NotNull ActionEnum action
) {
    public static UserMetricEventDTO create(Long userId, UserMetricEnum metric, ActionEnum action) {
        return new UserMetricEventDTO(
                userId,
                metric,
                action
        );
    }
}
