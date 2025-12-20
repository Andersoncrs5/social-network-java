package com.blog.writeapi.utils.annotations.valid.tag.uniqueTagBySlug;

import com.blog.writeapi.utils.annotations.valid.tag.uniqueTagByName.UniqueTagByNameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueTagBySlugValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueTagBySlug {
    String message() default "Name {value} already exist";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
