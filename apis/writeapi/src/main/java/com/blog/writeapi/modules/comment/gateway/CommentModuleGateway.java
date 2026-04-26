package com.blog.writeapi.modules.comment.gateway;

import com.blog.writeapi.modules.metric.dto.CommentMetricEventDTO;
import com.blog.writeapi.modules.metric.dto.PostMetricEventDTO;
import com.blog.writeapi.modules.metric.service.interfaces.IMetricService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentModuleGateway {

    private final IMetricService service;

    public void handleMetric(PostMetricEventDTO dto) {
        service.handleEventPost(dto);
    }

    public void handleMetricComment(CommentMetricEventDTO dto) {
        service.handleEventComment(dto);
    }

}
