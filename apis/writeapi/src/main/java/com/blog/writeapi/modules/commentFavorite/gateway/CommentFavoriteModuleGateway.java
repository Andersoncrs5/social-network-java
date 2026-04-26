package com.blog.writeapi.modules.commentFavorite.gateway;

import com.blog.writeapi.modules.metric.dto.CommentMetricEventDTO;
import com.blog.writeapi.modules.metric.service.interfaces.IMetricService;
import com.blog.writeapi.modules.userBlock.service.docs.IUserBlockService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentFavoriteModuleGateway {
    private final IUserBlockService userBlockService;
    private final IMetricService service;

    public void handleMetricComment(CommentMetricEventDTO dto) {
        service.handleEventComment(dto);
    }

    public boolean isBlocked(
            @IsId Long blockerId,
            @IsId Long blockedId
    ) {
        return this.userBlockService.isBlocked(blockerId, blockedId);
    }
}
