package com.blog.writeapi.services.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.models.CategoryModel;
import com.blog.writeapi.models.UserCategoryPreferenceModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.repositories.UserCategoryPreferenceRepository;
import com.blog.writeapi.services.interfaces.IUserCategoryPreferenceService;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserCategoryPreferenceService implements IUserCategoryPreferenceService {

    private final UserCategoryPreferenceRepository repository;
    private final Snowflake snowflake;

    @Override
    public void delete(@IsModelInitialized UserCategoryPreferenceModel model){
        repository.delete(model);
    }

    @Override
    public Optional<UserCategoryPreferenceModel> getByUserAndCategory(
            @IsModelInitialized UserModel user,
            @IsModelInitialized CategoryModel category
    ) {
        return repository.findByUserAndCategory(user, category);
    }

    @Override
    public UserCategoryPreferenceModel create(
            @IsModelInitialized UserModel user,
            @IsModelInitialized CategoryModel category
    ) {
        UserCategoryPreferenceModel model = new UserCategoryPreferenceModel().toBuilder()
                .id(this.snowflake.nextId())
                .category(category)
                .user(user)
                .build();

        return repository.save(model);
    }



}
