package com.blog.writeapi.utils.annotations.validations.post.existsPostById;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ExistsPostByIdValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistsPostById {
    String message() default "Post with ID {value} does not exist";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
