package com.blog.writeapi.modules.outbox.dto;

import com.blog.writeapi.utils.enums.outbox.AggregateTypeEnum;
import com.blog.writeapi.utils.enums.outbox.EventTypeEnum;
import com.blog.writeapi.utils.topic.TopicEnum;

public record CreateOutboxDTO(
        AggregateTypeEnum aggregateType,
        TopicEnum topic,
        EventTypeEnum eventType,
        String payload
) {
}
