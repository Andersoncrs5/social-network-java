package com.blog.writeapi.utils.annotations.validations.category.noCircularCategoryParent;

import com.blog.writeapi.modules.category.dtos.UpdateCategoryDTO;
import com.blog.writeapi.modules.category.models.CategoryModel;
import com.blog.writeapi.modules.category.repository.CategoryRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NoCircularCategoryParentValidator
        implements ConstraintValidator<NoCircularCategoryParent, UpdateCategoryDTO> {

    private final CategoryRepository categoryRepository;

    @Override
    public boolean isValid(UpdateCategoryDTO dto, ConstraintValidatorContext context) {

        if (dto.parentId() == null) { return true; }

        if (dto.id().equals(dto.parentId())) {
            buildViolation(context, "Category cannot be parent of itself.");
            return false;
        }

        CategoryModel parent = categoryRepository
                .findById(dto.parentId())
                .orElse(null);

        if (parent == null) { return true; }

        CategoryModel current = parent;
        while (current != null) {

            if (dto.id().equals(current.getId())) {
                buildViolation(context,
                        "Circular reference detected: a category cannot be its own ancestor.");
                return false;
            }

            current = current.getParent();
        }

        return true;
    }

    private void buildViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode("parentId")
                .addConstraintViolation();
    }
}