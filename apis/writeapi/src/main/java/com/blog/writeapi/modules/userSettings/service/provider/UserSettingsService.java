package com.blog.writeapi.modules.userSettings.service.provider;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.userSettings.dto.UpdateUserSettingsDTO;
import com.blog.writeapi.modules.userSettings.gateway.UserSettingsModuleGateway;
import com.blog.writeapi.modules.userSettings.model.UserSettingsModel;
import com.blog.writeapi.modules.userSettings.repository.UserSettingsRepository;
import com.blog.writeapi.modules.userSettings.service.interfaces.IUserSettingsService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.mappers.UserSettingsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSettingsService implements IUserSettingsService {

    private final UserSettingsRepository repository;
    private final UserSettingsModuleGateway gateway;
    private final Snowflake generator;
    private final UserSettingsMapper mapper;

    @Override
    public UserSettingsModel findByUserIdSimple(@IsId Long userId) {
        return repository.findByUserId(userId)
            .orElseThrow(() ->
                new ModelNotFoundException("User setting not found")
            );
    }

    @Override
    public UserSettingsModel create(@IsId Long userId) {
        UserModel user = this.gateway.findUserById(userId);

        UserSettingsModel settings = new UserSettingsModel().toBuilder()
                .id(generator.nextId())
                .user(user)
                .build();

        return repository.save(settings);
    }

    @Override
    public  UserSettingsModel update(
            UpdateUserSettingsDTO dto,
            @IsModelInitialized UserSettingsModel setting
    ) {
        this.mapper.merge(dto, setting);

        return repository.save(setting);
    }

}
