package com.blog.writeapi.utils.annotations.valid.global.slug;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = SlugValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Slug {

    String message() default "The slug must be lowercase, contain only lowercase letters, numbers, and hyphens ('-'), and cannot have duplicate hyphens.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}