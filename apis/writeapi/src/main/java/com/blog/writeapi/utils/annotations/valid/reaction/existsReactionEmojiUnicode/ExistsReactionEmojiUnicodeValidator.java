package com.blog.writeapi.utils.annotations.valid.reaction.existsReactionEmojiUnicode;

import com.blog.writeapi.services.interfaces.IReactionService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExistsReactionEmojiUnicodeValidator implements ConstraintValidator<ExistsReactionEmojiUnicode, String> {

    private final IReactionService service;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        Boolean exists = this.service.existsByEmojiUnicode(value);

        if (!exists) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    context.getDefaultConstraintMessageTemplate().replace("{value}", value)
            ).addConstraintViolation();
        }

        return !exists;
    }
}
