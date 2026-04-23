package com.blog.writeapi.modules.post.services.interfaces;

import com.blog.writeapi.modules.post.dtos.CreatePostDTO;
import com.blog.writeapi.modules.post.dtos.UpdatePostDTO;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.global.slugConstraint.SlugConstraint;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

public interface IPostService {
    Optional<PostModel> getById(@IsId Long id);
    PostModel getByIdSimple(@IsId Long id);
    Boolean existsById(@IsId Long id);
    Boolean existsBySlug(@SlugConstraint String slug);
    Optional<PostModel> getBySlug(@SlugConstraint String slug);
    PostModel create(@NotNull CreatePostDTO dto, @IsModelInitialized UserModel user);
    void delete(@IsModelInitialized PostModel post);
    PostModel update(@NotNull UpdatePostDTO dto, @IsModelInitialized PostModel post);
    void deleteAndCount(@IsId Long id);
}
