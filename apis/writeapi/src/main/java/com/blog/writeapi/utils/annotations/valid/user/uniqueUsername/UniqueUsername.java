package com.blog.writeapi.utils.annotations.valid.user.uniqueUsername;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueUsernameValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueUsername {
    String message() default "Username already exist";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}