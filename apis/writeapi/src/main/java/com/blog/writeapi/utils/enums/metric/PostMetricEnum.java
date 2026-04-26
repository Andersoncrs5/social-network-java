package com.blog.writeapi.utils.enums.metric;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostMetricEnum {
    UPVOTE("UPVOTE"),
    DOWNVOTE("DOWNVOTE"),
    FAVORITE("FAVORITE"),
    COMMENT("COMMENT"),
    REPORT("REPORT"),
    SHARE("SHARE"),
    REACTION("REACTION"),
    PARENT("PARENT"),
    VIEW("VIEW");

    private final String delta;
}