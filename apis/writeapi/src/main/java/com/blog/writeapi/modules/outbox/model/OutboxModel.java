package com.blog.writeapi.modules.outbox.model;

import com.blog.writeapi.utils.bases.models.BaseEntity;
import com.blog.writeapi.utils.enums.outbox.AggregateTypeEnum;
import com.blog.writeapi.utils.enums.outbox.EventTypeEnum;
import com.blog.writeapi.utils.enums.outbox.OutboxStatus;
import com.blog.writeapi.utils.topic.TopicEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(
        name = "outbox",
        indexes = {
            @Index(name = "idx_outbox_status", columnList = "status"),
            @Index(name = "idx_outbox_created_at", columnList = "createdAt")
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class OutboxModel extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AggregateTypeEnum aggregateType;

    @Column(nullable = false)
    private TopicEnum topic;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventTypeEnum eventType;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboxStatus status;

    private int retryCount = 0;

    private String errorMessage;

    @SneakyThrows
    public <T> void setPayloadFromObject(T object) {
        ObjectMapper mapper = new ObjectMapper();
        this.payload = mapper.writeValueAsString(object);
    }

}
