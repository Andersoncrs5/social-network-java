package com.blog.writeapi.utils.annotations.validations.tag.existsTagById;

import com.blog.writeapi.modules.tag.repository.TagRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ExistsTagByIdValidator implements ConstraintValidator<ExistsTagById, Long> {

    private final TagRepository repository;

    @Override
    @Transactional(readOnly = true)
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        boolean exists = this.repository.existsById(value);

        if (!exists) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    context.getDefaultConstraintMessageTemplate().replace("{value}", value.toString())
            ).addConstraintViolation();
        }

        return exists;
    }
}
