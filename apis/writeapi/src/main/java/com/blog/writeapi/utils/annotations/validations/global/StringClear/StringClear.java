package com.blog.writeapi.utils.annotations.validations.global.StringClear;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StringClearValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface StringClear {
    String message() default "Name can contain only letters, numbers and spaces.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
