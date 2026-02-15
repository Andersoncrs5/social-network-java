package com.blog.writeapi.utils.annotations.validations.postTag.uniqueTagInPost;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueTagInPostValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface UniqueTagInPost {
    String message() default "This tag with ID: {value} already exists in post";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
