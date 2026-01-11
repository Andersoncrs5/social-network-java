package com.blog.writeapi.utils.annotations.valid.reaction.existsReactionEmojiUnicode;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ExistsReactionEmojiUnicodeValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistsReactionEmojiUnicode {
    String message() default "Reaction with emoji unicode {value} does not exist";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
