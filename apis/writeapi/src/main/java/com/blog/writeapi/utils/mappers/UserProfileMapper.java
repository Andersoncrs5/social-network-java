package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.configs.mapper.CentralMapperConfig;
import com.blog.writeapi.modules.userProfile.dtos.UpdateUserProfileDTO;
import com.blog.writeapi.modules.userProfile.dtos.UserProfileDTO;
import com.blog.writeapi.modules.userProfile.models.UserProfileModel;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class,
        uses = {UserMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserProfileMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    void merge(UpdateUserProfileDTO dto, @MappingTarget UserProfileModel category);

    UserProfileDTO toDTO(@NotNull UserProfileModel model);

}
