package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.configs.mapper.CentralMapperConfig;
import com.blog.writeapi.modules.userSettings.dto.UpdateUserSettingsDTO;
import com.blog.writeapi.modules.userSettings.dto.UserSettingsDTO;
import com.blog.writeapi.modules.userSettings.model.UserSettingsModel;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {
                UserMapper.class
        },
        config = CentralMapperConfig.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserSettingsMapper {

    UserSettingsDTO toDTO(@IsModelInitialized UserSettingsModel settings);
    UserSettingsModel toModel(UserSettingsDTO settings);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void merge(UpdateUserSettingsDTO dto, @MappingTarget @IsModelInitialized UserSettingsModel settings);
}
