package com.blog.writeapi.modules.user.service.docs;

import com.blog.writeapi.modules.user.dtos.CreateUserDTO;
import com.blog.writeapi.modules.user.dtos.UpdateUserDTO;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.global.emailConstraint.EmailConstraint;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;

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
