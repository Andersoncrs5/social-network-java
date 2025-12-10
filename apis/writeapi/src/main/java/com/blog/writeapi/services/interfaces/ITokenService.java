package com.blog.writeapi.services.interfaces;

import com.blog.writeapi.models.RoleModel;
import com.blog.writeapi.models.UserModel;

import java.util.List;

public interface ITokenService {
    String generateToken(UserModel user, List<RoleModel> roles);
}
