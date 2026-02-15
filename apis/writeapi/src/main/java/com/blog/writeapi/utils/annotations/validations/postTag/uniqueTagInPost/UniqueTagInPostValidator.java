package com.blog.writeapi.utils.annotations.validations.postTag.uniqueTagInPost;

import com.blog.writeapi.modules.postTag.dtos.CreatePostTagDTO;
import com.blog.writeapi.modules.postTag.service.docs.IPostTagService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueTagInPostValidator implements ConstraintValidator<UniqueTagInPost, CreatePostTagDTO> {

    private final IPostTagService service;

    @Override
    public boolean isValid(CreatePostTagDTO dto, ConstraintValidatorContext context) {
        boolean alreadyExists = this.service.existsByPostAndTag(dto.postId(), dto.tagId());

        if (alreadyExists) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    context.getDefaultConstraintMessageTemplate().replace("{value}", dto.tagId().toString())
            ).addConstraintViolation();

            return false;
        }

        return true;
    }
}
