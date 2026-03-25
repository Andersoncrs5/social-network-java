package com.blog.writeapi.modules.userBlock.gateway;
import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.user.service.docs.IUserService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserBlockModuleGateway {
    private final IUserService userService;
    private final Snowflake snowflakeIdGenerator;

    public Long generateId() {
        return this.snowflakeIdGenerator.nextId();
    }

    public UserModel findByUserId(@IsId Long id) {
        return userService.GetByIdSimple(id);
    }

}
