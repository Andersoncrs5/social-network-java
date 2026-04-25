package com.blog.writeapi.unit.outbox.job;

import com.blog.writeapi.modules.outbox.job.OutboxJob;
import com.blog.writeapi.modules.outbox.model.OutboxModel;
import com.blog.writeapi.modules.outbox.repository.OutboxRepository;
import com.blog.writeapi.utils.enums.outbox.OutboxStatus;
import com.blog.writeapi.utils.topic.TopicEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OutboxJobTest {

    @Mock private KafkaTemplate<String, Object> kafkaTemplate;
    @Mock private OutboxRepository repository;

    @InjectMocks
    private OutboxJob outboxJob;

    private OutboxModel createOutboxModel(Long id, OutboxStatus status) {
        return OutboxModel.builder()
                .id(id)
                .topic(TopicEnum.POST_CREATED)
                .payload("{\"test\":\"data\"}")
                .status(status)
                .retryCount(0)
                .build();
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldPublishPendingEventsSuccessfully() throws Exception {
        OutboxModel event = createOutboxModel(1L, OutboxStatus.PENDING);
        Page<OutboxModel> page = new PageImpl<>(List.of(event));

        when(repository.findAllByStatus(eq(OutboxStatus.PENDING), any(Pageable.class)))
                .thenReturn(page);

        when(kafkaTemplate.send(anyString(), any()))
                .thenReturn(mock(CompletableFuture.class));

        outboxJob.publishPendingEvents();

        assertThat(event.getStatus()).isEqualTo(OutboxStatus.PROCESSED);
        verify(repository).save(event);
    }

    @Test
    void shouldMarkAsFailedWhenKafkaFails() {
        OutboxModel event = createOutboxModel(2L, OutboxStatus.PENDING);
        Page<OutboxModel> page = new PageImpl<>(List.of(event));

        when(repository.findAllByStatus(eq(OutboxStatus.PENDING), any(Pageable.class)))
                .thenReturn(page);

        when(kafkaTemplate.send(anyString(), any()))
                .thenThrow(new RuntimeException("Kafka down"));

        outboxJob.publishPendingEvents();

        assertThat(event.getStatus()).isEqualTo(OutboxStatus.FAILED);
        assertThat(event.getErrorMessage()).isEqualTo("Kafka down");
        verify(repository).save(event);
    }

    @Test
    void shouldDeleteProcessedEvents() {
        when(repository.deleteAllByStatus(OutboxStatus.PROCESSED)).thenReturn(5);

        outboxJob.removeEvent();

        verify(repository, times(1)).deleteAllByStatus(OutboxStatus.PROCESSED);
    }
}