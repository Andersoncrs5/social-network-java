package com.blog.writeapi.services.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.dtos.userProfile.UpdateUserProfileDTO;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.models.UserProfileModel;
import com.blog.writeapi.repositories.UserProfileRepository;
import com.blog.writeapi.services.interfaces.IUserProfileService;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.mappers.UserProfileMapper;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class UserProfileService implements IUserProfileService {

    private final UserProfileRepository repository;
    private final Snowflake generator;
    private final UserProfileMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Boolean existsByUser(@IsModelInitialized UserModel user) {
        return this.repository.existsByUser(user);
    }

    @Override
    @Transactional
    public Optional<UserProfileModel> getByUser(@IsModelInitialized UserModel user) {
        return this.repository.findByUser(user);
    }

    @Override
    @Transactional
    @Retry(name = "delete-retry")
    public void delete(@IsModelInitialized UserProfileModel profile) { this.repository.delete(profile); }

    @Override
    @Transactional
    @Retry(name = "create-retry")
    public UserProfileModel create(@IsModelInitialized UserModel user) {
        UserProfileModel profile = new UserProfileModel().toBuilder()
                .id(generator.nextId())
                .user(user)
                .build();

        return this.repository.save(profile);
    }

    @Override
    @Transactional
    @Retry(name = "update-retry")
    public UserProfileModel update(@IsModelInitialized UserProfileModel model, UpdateUserProfileDTO dto) {
        this.mapper.merge(dto, model);

        return this.repository.save(model);
    }

}
