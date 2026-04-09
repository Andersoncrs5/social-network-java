package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.configs.mapper.CentralMapperConfig;
import com.blog.writeapi.modules.userTagPreference.dtos.UserTagPreferenceDTO;
import com.blog.writeapi.modules.userTagPreference.models.UserTagPreferenceModel;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class,
        uses = {
                TagMapper.class, UserMapper.class
        },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserTagPreferenceMapper {
    UserTagPreferenceDTO toDTO(@IsModelInitialized UserTagPreferenceModel model);
}
