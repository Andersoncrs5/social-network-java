package com.blog.writeapi.utils.annotations.validations.image.fileContentType;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FileContentTypeValidator.class) // Aponta para a lógica
@Documented
public @interface FileContentType {
    String message() default "Tipo de arquivo não permitido";
    String[] allowed(); // Lista de tipos (ex: {"image/jpeg", "image/png"})

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}