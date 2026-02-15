package com.blog.writeapi.utils.annotations.validations.PostCategories.uniquePrimairyInPost;

import com.blog.writeapi.modules.postCategory.dtos.CreatePostCategoriesDTO;
import com.blog.writeapi.modules.postCategory.service.docs.IPostCategoriesService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniquePrimaryCategoryInPostValidator
        implements ConstraintValidator<UniquePrimaryCategoryInPost, CreatePostCategoriesDTO> {

    private final IPostCategoriesService service;

    @Override
    public boolean isValid(CreatePostCategoriesDTO dto, ConstraintValidatorContext context) {
        if (!dto.primary()) {
            return true;
        }

        boolean alreadyHasPrimary = service.existsByPostIdAndPrimaryTrue(dto.postId());

        return !alreadyHasPrimary;
    }
}