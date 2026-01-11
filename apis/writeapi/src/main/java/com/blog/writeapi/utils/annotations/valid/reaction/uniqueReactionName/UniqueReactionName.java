package com.blog.writeapi.utils.annotations.valid.reaction.uniqueReactionName;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.lang.annotation.*;

@NotBlank
@Size(min = 2, max = 200)
@Documented
@Constraint(validatedBy = UniqueReactionNameValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
public @interface UniqueReactionName {
    String message() default "This reaction with name: {value} already exists";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
