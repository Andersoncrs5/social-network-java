package com.blog.writeapi.utils.enums.metric;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserMetricEnum {
    POST("POST"),
    COMMENT("COMMENT"),
    FOLLOWING("FOLLOWING"),
    FOLLOW("FOLLOW"),
    USER_VIEW_RECEIVED("USER_VIEW_RECEIVED"),
    REPORT_RECEIVED("REPORT_RECEIVED");

    private final String delta;
}
