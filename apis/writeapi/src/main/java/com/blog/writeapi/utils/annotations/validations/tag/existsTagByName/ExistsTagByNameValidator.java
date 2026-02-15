package com.blog.writeapi.utils.annotations.validations.tag.existsTagByName;

import com.blog.writeapi.modules.tag.repository.TagRepository;
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

        boolean exists = this.repository.existsBySlugIgnoreCase(value);

        if (!exists) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    context.getDefaultConstraintMessageTemplate().replace("{value}", value)
            ).addConstraintViolation();
        }

        return exists;
    }
}
