package com.blog.writeapi.utils.enums.metric;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommentMetricEnum {
    UPVOTE("UPVOTE"),
    DOWNVOTE("DOWNVOTE"),
    FAVORITE("FAVORITE"),
    PARENT("PARENT"),
    REPORT("REPORT"),
    SHARE("SHARE"),
    REACTION("REACTION"),
    VIEW("VIEW");

    private final String delta;
}
