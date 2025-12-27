package com.blog.writeapi.services.interfaces;

import com.blog.writeapi.dtos.tag.CreateTagDTO;
import com.blog.writeapi.dtos.tag.UpdateTagDTO;
import com.blog.writeapi.models.TagModel;

import java.util.Optional;

public interface ITagService {
    TagModel getByIdSimple(Long id);
    Optional<TagModel> getById(Long id);
    Boolean existsById(Long id);
    Optional<TagModel> getByName(String name);
    Boolean existsByName(String name);
    Optional<TagModel> getBySlug(String slug);
    Boolean existsBySlug(String slug);
    void delete(TagModel tag);
    TagModel create(CreateTagDTO dto);
    TagModel update(UpdateTagDTO dto, TagModel tag);
}
