package com.blog.writeapi.utils.annotations.validations.tag.uniqueTagByName;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueTagByNameValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueTagByName {
    String message() default "Name {value} already exist";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
