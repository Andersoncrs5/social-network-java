package com.blog.writeapi.utils.mappers;

import com.blog.writeapi.dtos.user.CreateUserDTO;
import com.blog.writeapi.dtos.user.UpdateUserDTO;
import com.blog.writeapi.dtos.user.UserDTO;
import com.blog.writeapi.models.UserModel;
import com.github.dozermapper.core.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserMapper {
    private final Mapper mapper;

    public UserModel toModel(UserDTO dto) {
        return mapper.map(dto, UserModel.class);
    }

    public UserDTO toDTO(UserModel user) {
        return mapper.map(user, UserDTO.class);
    }

    public void merge(UpdateUserDTO dto, UserModel target) {
        mapper.map(dto, target);
    }

}
