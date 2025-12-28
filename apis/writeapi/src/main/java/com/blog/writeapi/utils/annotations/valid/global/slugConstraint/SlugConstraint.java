package com.blog.writeapi.utils.annotations.valid.global.slugConstraint;

import com.blog.writeapi.utils.annotations.valid.global.slug.SlugValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import java.lang.annotation.*;

@NotBlank(message = "Slug cannot be empty")
@Documented
@Constraint(validatedBy = SlugValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface SlugConstraint {

    String message() default "The slug must be lowercase, contain only alphanumeric characters and single hyphens, and cannot start or end with a hyphen";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}