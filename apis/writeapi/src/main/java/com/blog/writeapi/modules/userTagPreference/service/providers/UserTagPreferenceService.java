package com.blog.writeapi.modules.userTagPreference.service.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.tag.models.TagModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.userTagPreference.models.UserTagPreferenceModel;
import com.blog.writeapi.modules.userTagPreference.repository.UserTagPreferenceRepository;
import com.blog.writeapi.modules.userTagPreference.service.docs.IUserTagPreferenceService;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.exceptions.BusinessRuleException;
import com.blog.writeapi.utils.exceptions.InternalServerErrorException;
import com.blog.writeapi.utils.exceptions.UniqueConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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
        if (!tag.getIsActive()) throw new BusinessRuleException("Tag is unactive");

        UserTagPreferenceModel model = new UserTagPreferenceModel().toBuilder()
                .id(this.snowflake.nextId())
                .tag(tag)
                .user(user)
                .build();

        try {
            return repository.save(model);
        } catch (DataIntegrityViolationException e) {
            String message = Optional.of(e.getMostSpecificCause())
                    .map(Throwable::getMessage)
                    .orElse("");

            if (message.contains("uk_user_tag_preferences")) {
                throw new UniqueConstraintViolationException("This item has already been added to this highlight.");
            }

            throw new BusinessRuleException("Database integrity error: " + message);
        } catch (Exception e) {
            throw new InternalServerErrorException("Error creating report association.");
        }
    }



}
