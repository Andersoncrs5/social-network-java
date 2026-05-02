package com.blog.writeapi.modules.followers.gateway;

import com.blog.writeapi.modules.metric.dto.UserMetricEventDTO;
import com.blog.writeapi.modules.metric.service.interfaces.IMetricService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FollowModuleGateway {

    private final IMetricService service;

    public void handleMetricUser(UserMetricEventDTO dto) { service.handleEventUser(dto); }

}
