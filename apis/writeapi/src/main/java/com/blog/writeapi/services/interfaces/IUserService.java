package com.blog.writeapi.services.interfaces;

import com.blog.writeapi.dtos.user.CreateUserDTO;
import com.blog.writeapi.dtos.user.UpdateUserDTO;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.utils.annotations.valid.global.emailConstraint.EmailConstraint;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;

import java.util.Optional;

public interface IUserService {
    UserModel GetByIdSimple(@IsId Long id);
    Optional<UserModel> GetById(@IsId Long id);
    Boolean ExistsById(@IsId Long id);
    void Delete(@IsModelInitialized UserModel user);
    UserModel Update(UpdateUserDTO dto, @IsModelInitialized UserModel user);
    UserModel Create(CreateUserDTO dto);
    Optional<UserModel> findByEmail(@EmailConstraint String email);
    UserModel UpdateSimple(@IsModelInitialized UserModel user);
    Boolean existsByUsername(String username);
}
