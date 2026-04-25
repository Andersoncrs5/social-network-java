package com.blog.writeapi.utils.enums.outbox;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AggregateTypeEnum {
    POST("POST"),
    COMMENT("COMMENT"),
    USER("USER"),
    STORY("STORY"),
    ATTACHMENT("ATTACHMENT");

    private final String value;
}