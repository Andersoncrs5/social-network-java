package com.blog.writeapi.modules.userCategoryPreference.service.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.category.models.CategoryModel;
import com.blog.writeapi.modules.userCategoryPreference.gateway.UserCategoryPreferenceModuleGateway;
import com.blog.writeapi.modules.userCategoryPreference.models.UserCategoryPreferenceModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.userCategoryPreference.repository.UserCategoryPreferenceRepository;
import com.blog.writeapi.modules.userCategoryPreference.service.docs.IUserCategoryPreferenceService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.classes.ResultToggle;
import com.blog.writeapi.utils.exceptions.BusinessRuleException;
import com.blog.writeapi.utils.exceptions.InternalServerErrorException;
import com.blog.writeapi.utils.exceptions.UniqueConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserCategoryPreferenceService implements IUserCategoryPreferenceService {

    private final UserCategoryPreferenceRepository repository;
    private final UserCategoryPreferenceModuleGateway gateway;
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
        if (!category.getIsActive()) throw new BusinessRuleException("Category is unactive");

        UserCategoryPreferenceModel model = new UserCategoryPreferenceModel().toBuilder()
                .id(this.snowflake.nextId())
                .category(category)
                .user(user)
                .build();

        try {
            return repository.save(model);
        } catch (DataIntegrityViolationException e) {
            String message = Optional.of(e.getMostSpecificCause())
                    .map(Throwable::getMessage)
                    .orElse("");

            if (message.contains("uk_user_category_preferences")) {
                throw new UniqueConstraintViolationException("This category has already been added .");
            }

            throw new BusinessRuleException("Database integrity error: " + message);
        } catch (Exception e) {
            throw new InternalServerErrorException("Error creating report association.");
        }
    }

    public ResultToggle<UserCategoryPreferenceModel> toggle(
            @IsId Long categoryId,
            @IsId Long userId
    ) {
        Optional<UserCategoryPreferenceModel> optional = repository.findByUserIdAndCategoryId(userId, categoryId);

        if (optional.isPresent()) {
            this.delete(optional.get());
            return ResultToggle.removed();
        }

        CategoryModel category = this.gateway.findByCategoryById(categoryId);
        UserModel user = this.gateway.findByUserId(userId);

        return ResultToggle.added(this.create(user, category));
    }

}
