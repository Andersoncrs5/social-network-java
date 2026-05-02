package com.blog.writeapi.utils.topic;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TopicEnum {
    POST_CREATED("posts.created"),
    POST_METRIC("posts.metric"),
    USER_METRIC("user.metric"),
    COMMENT_ADDED("comments.added"),
    COMMENT_METRIC("comments.metric"),
    USER_REGISTERED("users.registered");

    private final String topicName;
}