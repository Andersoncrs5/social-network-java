package com.blog.writeapi.utils.annotations.validations.post.uniqueSlugByPost;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueSlugByPostValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueSlugByPost {
    String message() default "Slug {value} already exist";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
