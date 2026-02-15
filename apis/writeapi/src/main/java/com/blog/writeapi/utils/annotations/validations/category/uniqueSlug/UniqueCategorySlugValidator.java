package com.blog.writeapi.utils.annotations.validations.category.uniqueSlug;

import com.blog.writeapi.modules.category.repository.CategoryRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UniqueCategorySlugValidator implements ConstraintValidator<UniqueCategorySlug, String> {

    @Autowired
    private CategoryRepository repository;

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null || s.isBlank()) return true;

        return !this.repository.existsBySlugIgnoreCase(s);
    }
}
