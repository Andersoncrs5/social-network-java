package com.blog.writeapi.utils.annotations.validations.category.noCircularCategoryParent;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NoCircularCategoryParentValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface NoCircularCategoryParent {

    String message() default "Circular category hierarchy detected.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}