package com.blog.writeapi.modules.outbox.job;

import com.blog.writeapi.modules.outbox.model.OutboxModel;
import com.blog.writeapi.modules.outbox.repository.OutboxRepository;
import com.blog.writeapi.utils.enums.outbox.OutboxStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @Slf4j
@RequiredArgsConstructor
public class OutboxJob {

    private final OutboxRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Scheduled(fixedDelay = 10000)
    public void publishPendingEvents() {
        log.debug("Checking for pending events in Outbox...");

        Pageable pageable = PageRequest.of(0, 100);
        Page<OutboxModel> pending = repository.findAllByStatus(OutboxStatus.PENDING, pageable);

        if (pending.isEmpty()) {
            log.debug("No pending events found.");
            return;
        }

        for (OutboxModel event : pending) {
            try {
                log.info("Publishing event ID: {} to topic: {}", event.getId(), event.getTopic().getTopicName());

                kafkaTemplate.send(event.getTopic().getTopicName(), event.getPayload()).get();

                event.setStatus(OutboxStatus.PROCESSED);
                log.info("Event ID: {} published successfully.", event.getId());
            } catch (Exception e) {
                log.error("Failed to publish event ID: {}. Error: {}", event.getId(), e.getMessage());
                event.setStatus(OutboxStatus.FAILED);
                event.setErrorMessage(e.getMessage());
            } finally {
                event.setRetryCount(event.getRetryCount() + 1);
                repository.save(event);
            }
        }
    }

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void removeEvent() {
        log.info("Starting cleanup of processed events...");
        int deleted = repository.deleteAllByStatus(OutboxStatus.PROCESSED);
        log.info("Cleanup completed. {} processed events removed.", deleted);
    }



}