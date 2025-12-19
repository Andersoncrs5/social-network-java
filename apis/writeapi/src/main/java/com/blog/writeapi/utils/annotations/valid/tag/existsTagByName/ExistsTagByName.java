package com.blog.writeapi.utils.annotations.valid.tag.existsTagByName;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ExistsTagByNameValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistsTagByName {
    String message() default "Name {value} is in use!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
