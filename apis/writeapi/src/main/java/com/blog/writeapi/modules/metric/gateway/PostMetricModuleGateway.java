package com.blog.writeapi.modules.metric.gateway;

import com.blog.writeapi.modules.outbox.dto.CreateOutboxDTO;
import com.blog.writeapi.modules.outbox.model.OutboxModel;
import com.blog.writeapi.modules.outbox.service.interfaces.IOutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostMetricModuleGateway {

    private final IOutboxService service;

    public OutboxModel addEvent(CreateOutboxDTO dto) {
        return service.create(dto);
    }

}
