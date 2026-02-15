package com.blog.writeapi.utils.annotations.validations.global.forbidden;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ForbiddenMatchesValidator implements ConstraintValidator<ForbiddenMatches, String> {

    private List<String> forbiddenWords;
    private boolean ignoreCase;

    @Override
    public void initialize(ForbiddenMatches constraintAnnotation) {
        this.ignoreCase = constraintAnnotation.ignoreCase();

        this.forbiddenWords = Arrays.stream(constraintAnnotation.value())
                .map(word -> this.ignoreCase ? word.toLowerCase() : word)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }

        String targetValue = this.ignoreCase ? value.toLowerCase() : value;

        for (String forbiddenWord : forbiddenWords) {
            if (targetValue.equals(forbiddenWord)) {
                return false;
            }
        }

        return true;
    }
}
