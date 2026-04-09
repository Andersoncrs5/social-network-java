package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.configs.mapper.CentralMapperConfig;
import com.blog.writeapi.modules.user.dtos.CreateUserDTO;
import com.blog.writeapi.modules.user.dtos.UpdateUserDTO;
import com.blog.writeapi.modules.user.dtos.UserDTO;
import com.blog.writeapi.modules.user.models.UserModel;
import org.mapstruct.*;
import org.springframework.stereotype.Service;

@Service
@Mapper(
        config = CentralMapperConfig.class,
        componentModel = "spring"
)
public interface UserMapper {

    UserModel toModel(UserDTO dto);

    UserDTO toDTO(UserModel user);

    UserModel toModel(CreateUserDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "createdAt", ignore = true)
    void merge(UpdateUserDTO dto, @MappingTarget UserModel target);

}