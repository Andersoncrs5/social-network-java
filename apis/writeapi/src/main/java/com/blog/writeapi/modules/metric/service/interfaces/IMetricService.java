package com.blog.writeapi.modules.metric.service.interfaces;

import com.blog.writeapi.modules.metric.dto.CommentMetricEventDTO;
import com.blog.writeapi.modules.metric.dto.PostMetricEventDTO;

public interface IMetricService {
    void handleEventPost(PostMetricEventDTO dto);
    void handleEventComment(CommentMetricEventDTO dto);
}
