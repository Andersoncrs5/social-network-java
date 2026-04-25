package com.blog.writeapi.modules.outbox.dto;

import com.blog.writeapi.utils.enums.outbox.AggregateTypeEnum;
import com.blog.writeapi.utils.enums.outbox.EventTypeEnum;
import com.blog.writeapi.utils.enums.outbox.OutboxStatus;
import com.blog.writeapi.utils.topic.TopicEnum;

public record UpdateOutboxDTO(
        AggregateTypeEnum aggregateType,
        TopicEnum topic,
        EventTypeEnum eventType,
        OutboxStatus status,
        int retryCount,
        String errorMessage
) {
}
