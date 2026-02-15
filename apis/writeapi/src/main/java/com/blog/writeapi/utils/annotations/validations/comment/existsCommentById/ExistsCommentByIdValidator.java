package com.blog.writeapi.utils.annotations.validations.comment.existsCommentById;

import com.blog.writeapi.modules.comment.repository.CommentRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExistsCommentByIdValidator implements ConstraintValidator<ExistsCommentById, Long> {

    private final CommentRepository repository;

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
