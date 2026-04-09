package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.configs.mapper.CentralMapperConfig;
import com.blog.writeapi.modules.tag.dtos.CreateTagDTO;
import com.blog.writeapi.modules.tag.dtos.TagDTO;
import com.blog.writeapi.modules.tag.dtos.UpdateTagDTO;
import com.blog.writeapi.modules.tag.models.TagModel;
import org.mapstruct.*;

@Mapper(
        config = CentralMapperConfig.class,
        componentModel = "spring"
)
public interface TagMapper {

    TagModel toModel(TagDTO dto);

    TagDTO toDTO(TagModel dto);

    TagModel toModel(CreateTagDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void merge(UpdateTagDTO dto, @MappingTarget TagModel category);

}
