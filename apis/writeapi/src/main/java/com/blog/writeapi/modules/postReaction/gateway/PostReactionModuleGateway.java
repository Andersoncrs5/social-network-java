package com.blog.writeapi.modules.postReaction.gateway;

import com.blog.writeapi.modules.metric.dto.PostMetricEventDTO;
import com.blog.writeapi.modules.metric.service.interfaces.IMetricService;
import com.blog.writeapi.modules.userBlock.service.docs.IUserBlockService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostReactionModuleGateway {
    private final IUserBlockService userBlockService;
    private final IMetricService service;

    public void handleMetric(PostMetricEventDTO dto) {
        service.handleEventPost(dto);
    }

    public boolean isBlocked(
            @IsId Long blockerId,
            @IsId Long blockedId
    ) {
        return this.userBlockService.isBlocked(blockerId, blockedId);
    }
}
