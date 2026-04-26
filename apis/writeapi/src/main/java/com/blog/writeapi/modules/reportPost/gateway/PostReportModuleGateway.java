package com.blog.writeapi.modules.reportPost.gateway;

import com.blog.writeapi.modules.metric.dto.PostMetricEventDTO;
import com.blog.writeapi.modules.metric.service.interfaces.IMetricService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostReportModuleGateway {

    private final IMetricService service;

    public void handleMetric(PostMetricEventDTO dto) {
        service.handleEventPost(dto);
    }

}
