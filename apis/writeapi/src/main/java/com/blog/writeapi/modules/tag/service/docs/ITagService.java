package com.blog.writeapi.modules.tag.service.docs;

import com.blog.writeapi.modules.tag.dtos.CreateTagDTO;
import com.blog.writeapi.modules.tag.dtos.UpdateTagDTO;
import com.blog.writeapi.modules.tag.models.TagModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.global.slugConstraint.SlugConstraint;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;

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
