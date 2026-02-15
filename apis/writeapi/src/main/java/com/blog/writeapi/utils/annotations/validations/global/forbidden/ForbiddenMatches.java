package com.blog.writeapi.utils.annotations.validations.global.forbidden;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = ForbiddenMatchesValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ForbiddenMatches {

    String[] value() default {};
    String message() default "The value contains a reserved or prohibited word.";
    boolean ignoreCase() default true;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}