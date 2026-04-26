package com.blog.writeapi.modules.metric.service.provider;

import com.blog.writeapi.modules.metric.dto.CommentMetricEventDTO;
import com.blog.writeapi.modules.outbox.dto.CreateOutboxDTO;
import com.blog.writeapi.modules.metric.dto.PostMetricEventDTO;
import com.blog.writeapi.modules.metric.gateway.PostMetricModuleGateway;
import com.blog.writeapi.modules.metric.service.interfaces.IMetricService;
import com.blog.writeapi.utils.enums.outbox.AggregateTypeEnum;
import com.blog.writeapi.utils.enums.outbox.EventTypeEnum;
import com.blog.writeapi.utils.topic.TopicEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service @Slf4j
@RequiredArgsConstructor
public class MetricService implements IMetricService {

    private final PostMetricModuleGateway gateway;
    private final ObjectMapper objectMapper;

    public void handleEventPost(PostMetricEventDTO dto) {
        String payload = serialize(dto);

        CreateOutboxDTO outboxDTO = new CreateOutboxDTO(
                AggregateTypeEnum.POST,
                TopicEnum.POST_METRIC,
                EventTypeEnum.METRIC,
                payload
        );

        log.info("Payload event: {}", payload);
        gateway.addEvent(outboxDTO);
    }

    public void handleEventComment(CommentMetricEventDTO dto) {
        String payload = serialize(dto);

        CreateOutboxDTO outboxDTO = new CreateOutboxDTO(
                AggregateTypeEnum.COMMENT,
                TopicEnum.COMMENT_METRIC,
                EventTypeEnum.METRIC,
                payload
        );

        gateway.addEvent(outboxDTO);
    }

    private String serialize(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize PostMetricEventDTO", e);
            throw new SerializationException("Error serializing metric event", e);
        }
    }

}
