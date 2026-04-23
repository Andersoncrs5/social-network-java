package com.blog.writeapi.modules.userProfile.service.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.userProfile.dtos.UpdateUserProfileDTO;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.userProfile.models.UserProfileModel;
import com.blog.writeapi.modules.userProfile.repository.UserProfileRepository;
import com.blog.writeapi.modules.userProfile.service.docs.IUserProfileService;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.exceptions.InternalServerErrorException;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.exceptions.UniqueConstraintViolationException;
import com.blog.writeapi.utils.mappers.UserProfileMapper;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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
    @Transactional(readOnly = true)
    public UserProfileModel getByUserSimple(@IsModelInitialized UserModel user) {
        return this.getByUser(user).orElseThrow(() -> new ModelNotFoundException("Profile not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserProfileModel> getByUser(@IsModelInitialized UserModel user) {
        return this.repository.findByUser(user);
    }

    @Override
    @Transactional
    @Retry(name = "delete-retry")
    public void delete(@IsModelInitialized UserProfileModel profile) { this.repository.delete(profile); }

    @Override @Transactional
    @Retry(name = "create-retry")
    public UserProfileModel create(@IsModelInitialized UserModel user) {
        UserProfileModel profile = new UserProfileModel().toBuilder()
                .id(generator.nextId())
                .user(user)
                .build();

        try {
            return this.repository.save(profile);
        } catch (DataIntegrityViolationException e) {
            String message = Optional.of(e.getMostSpecificCause())
                    .map(Throwable::getMessage)
                    .orElse("")
                    .toLowerCase();

            if (message.contains("idx_user_id_profiles")) {
                throw new UniqueConstraintViolationException("Profile already exists for this user.");
            }

            throw new InternalServerErrorException("Data integrity violation while creating user profile.");
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while creating user profile", e);
        }
    }

    @Override
    @Transactional
    @Retry(name = "update-retry")
    public UserProfileModel update(@IsModelInitialized UserProfileModel model, UpdateUserProfileDTO dto) {
        this.mapper.merge(dto, model);

        return this.repository.save(model);
    }

}
