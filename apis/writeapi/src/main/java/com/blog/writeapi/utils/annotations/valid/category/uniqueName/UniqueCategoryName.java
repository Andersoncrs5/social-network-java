package com.blog.writeapi.utils.annotations.valid.category.uniqueName;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueCategoryNameValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueCategoryName {
    String message() default "Name already exist";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
