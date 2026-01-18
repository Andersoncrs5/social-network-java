package com.blog.writeapi.services.interfaces;

import com.blog.writeapi.dtos.tag.CreateTagDTO;
import com.blog.writeapi.dtos.tag.UpdateTagDTO;
import com.blog.writeapi.models.TagModel;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.annotations.valid.global.slugConstraint.SlugConstraint;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;

import java.util.Optional;

public interface ITagService {
    TagModel getByIdSimple(@IsId Long id);
    Optional<TagModel> getById(@IsId Long id);
    Boolean existsById(@IsId Long id);
    Optional<TagModel> getByName(String name);
    Boolean existsByName(String name);
    Optional<TagModel> getBySlug(@SlugConstraint String slug);
    Boolean existsBySlug(@SlugConstraint String slug);
    void delete(@IsModelInitialized TagModel tag);
    TagModel create(CreateTagDTO dto);
    TagModel update(UpdateTagDTO dto, @IsModelInitialized TagModel tag);
}
