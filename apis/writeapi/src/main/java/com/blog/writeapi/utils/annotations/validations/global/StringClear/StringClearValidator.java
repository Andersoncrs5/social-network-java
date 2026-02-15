package com.blog.writeapi.utils.annotations.validations.global.StringClear;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class StringClearValidator implements ConstraintValidator<StringClear, String> {

    private static final String SLUG_REGEX = "^[\\p{L} ]+$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true;

        return value.matches(SLUG_REGEX);
    }
}
