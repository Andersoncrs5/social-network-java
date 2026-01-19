package com.blog.writeapi.utils.annotations.valid.tag.existsTagBySlug;

import com.blog.writeapi.repositories.TagRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ExistsTagBySlugValidator implements ConstraintValidator<ExistsTagBySlug, String> {

    private final TagRepository repository;

    @Override
    @Transactional(readOnly = true)
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        boolean exists = repository.existsBySlugIgnoreCase(value);

        if (!exists) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    context.getDefaultConstraintMessageTemplate()
            ).addConstraintViolation();
        }

        return exists;
    }

}
