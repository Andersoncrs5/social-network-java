package com.blog.writeapi.utils.enums.outbox;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventTypeEnum {
    CREATED("CREATED"),
    UPDATED("UPDATED"),
    DELETED("DELETED"),
    METRIC("METRIC"),
    REPORTED("REPORTED");

    private final String value;
}