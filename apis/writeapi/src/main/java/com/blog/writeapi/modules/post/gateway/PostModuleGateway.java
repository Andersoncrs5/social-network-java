package com.blog.writeapi.modules.post.gateway;

import com.blog.writeapi.modules.metric.dto.PostMetricEventDTO;
import com.blog.writeapi.modules.metric.service.interfaces.IMetricService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostModuleGateway {

    private final IMetricService service;

    public void handleMetric(PostMetricEventDTO dto) {
        service.handleEventPost(dto);
    }

}
