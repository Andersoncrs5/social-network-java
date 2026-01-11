package com.blog.writeapi.utils.annotations.valid.reaction.existsReactionName;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ExistsReactionNameValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistsReactionName {
    String message() default "Reaction with name {value} does not exist";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
