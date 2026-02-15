package com.blog.writeapi.utils.annotations.validations.post.uniqueSlugByPost;

import com.blog.writeapi.modules.post.repository.PostRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueSlugByPostValidator implements ConstraintValidator<UniqueSlugByPost, String> {

    private final PostRepository repository;


    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;

        boolean exists = this.repository.existsBySlugIgnoreCase(value);

        if (exists) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    context.getDefaultConstraintMessageTemplate().replace("{value}", value)
            ).addConstraintViolation();
        }

        return !exists;
    }
}
