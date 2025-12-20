package com.blog.writeapi.utils.annotations.valid.tag.existsTagByName;

import com.blog.writeapi.repositories.TagRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExistsTagByNameValidator implements ConstraintValidator<ExistsTagByName, String> {

    private final TagRepository repository;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) { return true; }

        boolean exists = this.repository.existsBySlug(value);

        if (!exists) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    context.getDefaultConstraintMessageTemplate().replace("{value}", value)
            ).addConstraintViolation();
        }

        return exists;
    }
}
