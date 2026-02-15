package com.blog.writeapi.utils.annotations.validations.tag.existsTagBySlug;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = ExistsTagBySlugValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistsTagBySlug {
    String message() default "Slug with ID {value} does not exist";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
