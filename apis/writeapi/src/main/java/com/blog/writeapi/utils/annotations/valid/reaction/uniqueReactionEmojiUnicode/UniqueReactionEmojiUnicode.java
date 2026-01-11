package com.blog.writeapi.utils.annotations.valid.reaction.uniqueReactionEmojiUnicode;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.lang.annotation.*;

@Size(max = 20)
@Documented
@Constraint(validatedBy = UniqueReactionEmojiUnicodeValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
public @interface UniqueReactionEmojiUnicode {
    String message() default "This reaction with emoji unicode: {value} already exists";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
