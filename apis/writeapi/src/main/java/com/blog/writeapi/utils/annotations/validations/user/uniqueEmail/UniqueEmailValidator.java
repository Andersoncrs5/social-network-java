package com.blog.writeapi.utils.annotations.validations.user.uniqueEmail;

import com.blog.writeapi.modules.user.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {
    @Autowired
    private UserRepository repository;

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s.isBlank()) return false;

        return !this.repository.existsByEmailIgnoreCase(s);
    }
}
