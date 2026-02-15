package com.blog.writeapi.modules.userCategoryPreference.service.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.category.models.CategoryModel;
import com.blog.writeapi.modules.userCategoryPreference.models.UserCategoryPreferenceModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.userCategoryPreference.repository.UserCategoryPreferenceRepository;
import com.blog.writeapi.modules.userCategoryPreference.service.docs.IUserCategoryPreferenceService;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
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
