package com.blog.writeapi.unit.outbox.service;

import com.blog.writeapi.modules.outbox.dto.CreateOutboxDTO;
import com.blog.writeapi.modules.outbox.gateway.OutboxModuleGateway;
import com.blog.writeapi.modules.outbox.model.OutboxModel;
import com.blog.writeapi.modules.outbox.repository.OutboxRepository;
import com.blog.writeapi.modules.outbox.service.provider.OutboxService;
import com.blog.writeapi.utils.enums.outbox.AggregateTypeEnum;
import com.blog.writeapi.utils.enums.outbox.EventTypeEnum;
import com.blog.writeapi.utils.enums.outbox.OutboxStatus;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.topic.TopicEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OutboxServiceTest {

    @Mock private OutboxRepository repository;
    @Mock private OutboxModuleGateway gateway;

    @InjectMocks private OutboxService service;

    OutboxModel outbox = new OutboxModel().toBuilder()
            .id(3776236454923453433L)
            .aggregateType(AggregateTypeEnum.COMMENT)
            .topic(TopicEnum.COMMENT_ADDED)
            .eventType(EventTypeEnum.CREATED)
            .payload("{ \"name\": \"pochita\" }")
            .retryCount(32)
            .errorMessage(null)
            .build();

    CreateOutboxDTO dto = new CreateOutboxDTO(
            outbox.getAggregateType(),
            outbox.getTopic(),
            outbox.getEventType(),
            outbox.getPayload()
    );

    @Test
    void shouldSave() {
        when(repository.save(any())).thenReturn(outbox);

        OutboxModel model = this.service.create(dto);

        assertThat(model.getId()).isEqualTo(outbox.getId());

        verify(repository, times(1)).save(any());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldDeleteById() {
        when(repository.deleteAndCount(outbox.getId()))
                .thenReturn(1);

        this.service.deleteById(outbox.getId());

        verify(repository, times(1)).deleteAndCount(outbox.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldThrowModelNotFoundExceptionWhenDeleteById() {
        when(repository.deleteAndCount(outbox.getId()))
                .thenReturn(0);

        assertThatThrownBy(() -> this.service.deleteById(outbox.getId()))
                .isInstanceOf(ModelNotFoundException.class)
                .hasMessageContaining("Outbox event not found");

        verify(repository, times(1)).deleteAndCount(outbox.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldFindAllByStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        List<OutboxModel> outboxList = List.of(outbox);
        Page<OutboxModel> page = new PageImpl<>(outboxList, pageable, outboxList.size());

        when(repository.findAllByStatus(OutboxStatus.PENDING, pageable))
                .thenReturn(page);

        Page<OutboxModel> result = this.service.findAllByStatus(OutboxStatus.PENDING, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(outbox);

        verify(repository, times(1)).findAllByStatus(OutboxStatus.PENDING, pageable);
    }

    @Test
    void shouldReturnEmptyPageWhenNoOutboxFoundByStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<OutboxModel> emptyPage = Page.empty(pageable);

        when(repository.findAllByStatus(OutboxStatus.FAILED, pageable))
                .thenReturn(emptyPage);

        Page<OutboxModel> result = this.service.findAllByStatus(OutboxStatus.FAILED, pageable);

        assertThat(result.isEmpty()).isTrue();

        verify(repository, times(1)).findAllByStatus(OutboxStatus.FAILED, pageable);
    }

}
