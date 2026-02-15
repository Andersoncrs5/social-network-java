package com.blog.writeapi.services.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.models.TagModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.models.UserTagPreferenceModel;
import com.blog.writeapi.repositories.UserTagPreferenceRepository;
import com.blog.writeapi.services.interfaces.IUserTagPreferenceService;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserTagPreferenceService implements IUserTagPreferenceService {

    private final UserTagPreferenceRepository repository;
    private final Snowflake snowflake;

    @Override
    public void delete(@IsModelInitialized UserTagPreferenceModel model){
        repository.delete(model);
    }

    @Override
    public Optional<UserTagPreferenceModel> getByUserAndTag(
            @IsModelInitialized UserModel user,
            @IsModelInitialized TagModel tag
    ) {
        return repository.findByUserAndTag(user, tag);
    }

    @Override
    public UserTagPreferenceModel create(
            @IsModelInitialized UserModel user,
            @IsModelInitialized TagModel tag
    ) {
        UserTagPreferenceModel model = new UserTagPreferenceModel().toBuilder()
                .id(this.snowflake.nextId())
                .tag(tag)
                .user(user)
                .build();

        return repository.save(model);
    }



}
