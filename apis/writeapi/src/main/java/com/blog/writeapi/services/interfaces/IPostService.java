package com.blog.writeapi.services.interfaces;

import com.blog.writeapi.dtos.post.CreatePostDTO;
import com.blog.writeapi.dtos.post.UpdatePostDTO;
import com.blog.writeapi.models.PostModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.annotations.valid.global.slugConstraint.SlugConstraint;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;
import jakarta.validation.constraints.NotBlank;
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
}
