package com.blog.writeapi.utils.annotations.valid.post.existsPostById;

import com.blog.writeapi.repositories.PostCategoriesRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExistsPostByIdValidator implements ConstraintValidator<ExistsPostById, Long> {

    private final PostCategoriesRepository repository;

    @Override
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
