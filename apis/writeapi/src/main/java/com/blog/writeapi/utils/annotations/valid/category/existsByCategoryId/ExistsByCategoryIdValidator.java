package com.blog.writeapi.utils.annotations.valid.category.existsByCategoryId;

import com.blog.writeapi.repositories.CategoryRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExistsByCategoryIdValidator implements ConstraintValidator<ExistsByCategoryId, Long> {

    private final CategoryRepository repository;

    @Override
    public boolean isValid(Long s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null || s <= 0) return true;

        return this.repository.existsById(s);
    }
}
