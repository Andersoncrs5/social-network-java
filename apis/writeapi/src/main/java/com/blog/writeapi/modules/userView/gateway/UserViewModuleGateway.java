package com.blog.writeapi.modules.userView.gateway;

import com.blog.writeapi.modules.metric.dto.UserMetricEventDTO;
import com.blog.writeapi.modules.metric.service.interfaces.IMetricService;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.user.service.docs.IUserService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserViewModuleGateway {

    private final IUserService userService;
    private final IMetricService service;

    public void handleMetricUser(UserMetricEventDTO dto) { service.handleEventUser(dto); }

    public UserModel findUserById(@IsId Long id) {
        return userService.GetByIdSimple(id);
    }

}
