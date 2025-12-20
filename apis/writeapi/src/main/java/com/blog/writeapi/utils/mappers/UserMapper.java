package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.dtos.user.CreateUserDTO;
import com.blog.writeapi.dtos.user.UpdateUserDTO;
import com.blog.writeapi.dtos.user.UserDTO;
import com.blog.writeapi.models.UserModel;
import org.mapstruct.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Service
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserModel toModel(UserDTO dto);

    UserDTO toDTO(UserModel user);

    UserModel toModel(CreateUserDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "createdAt", ignore = true)
    void merge(UpdateUserDTO dto, @MappingTarget UserModel target);

    default OffsetDateTime map(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.atOffset(ZoneOffset.UTC);
    }

    default LocalDateTime map(OffsetDateTime offsetDateTime) {
        return offsetDateTime == null ? null : offsetDateTime.toLocalDateTime();
    }
}