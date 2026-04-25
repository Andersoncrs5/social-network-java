package com.blog.writeapi.modules.outbox.service.provider;

import com.blog.writeapi.modules.outbox.dto.CreateOutboxDTO;
import com.blog.writeapi.modules.outbox.dto.UpdateOutboxDTO;
import com.blog.writeapi.modules.outbox.gateway.OutboxModuleGateway;
import com.blog.writeapi.modules.outbox.model.OutboxModel;
import com.blog.writeapi.modules.outbox.repository.OutboxRepository;
import com.blog.writeapi.modules.outbox.service.interfaces.IOutboxService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.enums.outbox.OutboxStatus;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;
import java.util.Optional;

@Validated
@Service @Slf4j
@RequiredArgsConstructor
public class OutboxService implements IOutboxService {

    private final OutboxRepository repository;
    private final OutboxModuleGateway gateway;

    public OutboxModel create(CreateOutboxDTO dto) {
        log.debug("Preparing to save the event to Outbox.");

        OutboxModel outbox = OutboxModel.builder()
                .aggregateType(dto.aggregateType())
                .topic(dto.topic())
                .eventType(dto.eventType())
                .payload(dto.payload())
                .status(OutboxStatus.PENDING)
                .retryCount(0)
                .build();

        OutboxModel save = repository.save(outbox);
        log.debug("event saved in outbox");
        return save;
    }

    public void deleteById(@IsId Long id) {
        int result = repository.deleteAndCount(id);

        if (Objects.equals(result, 0)) {
            throw new ModelNotFoundException("Outbox event not found");
        }
    }

    public OutboxModel update(OutboxModel outbox, UpdateOutboxDTO dto) {
        Optional.ofNullable(dto.aggregateType()).ifPresent(outbox::setAggregateType);
        Optional.ofNullable(dto.topic()).ifPresent(outbox::setTopic);
        Optional.ofNullable(dto.eventType()).ifPresent(outbox::setEventType);
        Optional.ofNullable(dto.status()).ifPresent(outbox::setStatus);
        Optional.ofNullable(dto.errorMessage()).ifPresent(outbox::setErrorMessage);

        if (dto.retryCount() >= 0) {
            outbox.setRetryCount(dto.retryCount());
        }

        return repository.save(outbox);
    }

    public Page<OutboxModel> findAllByStatus(OutboxStatus status, Pageable pageable) {
        return repository.findAllByStatus(status, pageable);
    }

}
