package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.dtos.user.CreateUserDTO;
import com.blog.writeapi.dtos.user.UpdateUserDTO;
import com.blog.writeapi.dtos.user.UserDTO;
import com.blog.writeapi.models.UserModel;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Service;

@Service
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserModel toModel(UserDTO dto);

    UserDTO toDTO(UserModel user);

    UserModel toModel(CreateUserDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void merge(UpdateUserDTO dto, @MappingTarget UserModel target);

}