package com.blog.writeapi.modules.reportType.utils.validations.uniqueReportTypeByName;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.lang.annotation.*;

@NotBlank
@Size(min = 2, max = 100)
@Documented
@Constraint(validatedBy = UniqueReportTypeByNameValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
public @interface UniqueReportTypeByName {
    String message() default "This report type with name: {value} already exists";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
