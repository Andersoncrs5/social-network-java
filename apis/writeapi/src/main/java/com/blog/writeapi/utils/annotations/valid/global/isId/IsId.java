package com.blog.writeapi.utils.annotations.valid.global.isId;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.lang.annotation.*;

@NotNull
@Positive
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@Documented
public @interface IsId {
    String message() default "The ID must be a positive number.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}