package com.blog.writeapi.utils.annotations.valid.category.existsByCategoryId;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ExistsByCategoryIdValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistsByCategoryId {

    String message() default "Category does not exist.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
