package com.blog.writeapi.modules.commentReport.gateway;

import com.blog.writeapi.modules.metric.dto.CommentMetricEventDTO;
import com.blog.writeapi.modules.metric.service.interfaces.IMetricService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentReportModuleGateway {
    private final IMetricService service;

    public void handleMetricComment(CommentMetricEventDTO dto) {
        service.handleEventComment(dto);
    }
}
