package com.blog.writeapi.utils.annotations.validations.reaction.uniqueReactionName;

import com.blog.writeapi.modules.reaction.service.docs.IReactionService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueReactionNameValidator implements ConstraintValidator<UniqueReactionName, String> {

    private final IReactionService service;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;

        boolean exists = this.service.existsByName(value);

        if (exists) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    context.getDefaultConstraintMessageTemplate().replace("{value}", value)
            ).addConstraintViolation();
        }

        return !exists;

    }
}
