package com.blog.writeapi.utils.annotations.valid.isModelInitialized;

import com.blog.writeapi.utils.bases.models.BaseEntity;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class ModelInitializedValidator implements ConstraintValidator<IsModelInitialized, BaseEntity> {

    private static final long MIN_SNOWFLAKE_VALUE = 100000000000000L;

    @Override
    public boolean isValid(BaseEntity value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        Long id = value.getId();

        return id != null &&
                id >= MIN_SNOWFLAKE_VALUE;
    }
}