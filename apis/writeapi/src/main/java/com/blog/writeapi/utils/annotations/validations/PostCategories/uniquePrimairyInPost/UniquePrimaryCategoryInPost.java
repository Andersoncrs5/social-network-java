package com.blog.writeapi.utils.annotations.validations.PostCategories.uniquePrimairyInPost;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniquePrimaryCategoryInPostValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniquePrimaryCategoryInPost {
    String message() default "Already exists a category with primary";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
