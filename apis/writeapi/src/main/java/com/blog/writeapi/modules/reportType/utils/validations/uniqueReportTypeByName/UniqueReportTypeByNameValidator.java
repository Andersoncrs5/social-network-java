package com.blog.writeapi.modules.reportType.utils.validations.uniqueReportTypeByName;

import com.blog.writeapi.modules.reportType.services.interfaces.IReportTypeService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueReportTypeByNameValidator implements ConstraintValidator<UniqueReportTypeByName, String> {

    private final IReportTypeService service;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;

        boolean exists = this.service.existsByName(value);

        if (exists) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    context.getDefaultConstraintMessageTemplate().replace("{value}", value)
            ).addConstraintViolation();
        }

        return !exists;
    }
}
