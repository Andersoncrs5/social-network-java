package com.blog.writeapi.services.interfaces;

import com.blog.writeapi.dtos.user.CreateUserDTO;
import com.blog.writeapi.dtos.user.UpdateUserDTO;
import com.blog.writeapi.models.UserModel;

import java.util.Optional;

public interface IUserService {
    Optional<UserModel> GetById(Long id);
    Boolean ExistsById(Long id);
    void Delete(UserModel user);
    UserModel Update(UpdateUserDTO dto, UserModel user);
    UserModel Create(CreateUserDTO dto);
}
