package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.dtos.tag.TagDTO;
import com.blog.writeapi.dtos.tag.CreateTagDTO;
import com.blog.writeapi.dtos.tag.UpdateTagDTO;
import com.blog.writeapi.models.TagModel;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TagMapper {

    TagModel toModel(TagDTO dto);
    TagDTO toDTO(TagModel category);

    TagModel toModel(CreateTagDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void merge(UpdateTagDTO dto, @MappingTarget TagModel category);

}
