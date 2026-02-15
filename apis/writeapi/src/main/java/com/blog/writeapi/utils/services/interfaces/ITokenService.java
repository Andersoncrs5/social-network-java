package com.blog.writeapi.utils.services.interfaces;

import com.blog.writeapi.modules.role.models.RoleModel;
import com.blog.writeapi.modules.user.models.UserModel;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

public interface ITokenService {
    String generateToken(UserModel user, List<RoleModel> roles);
    String validateToken(String token);
    String generateRefreshToken(UserModel user);
    Map<String, Object> extractAllClaims(String token);
    String extractSubjectFromRequest(HttpServletRequest request);
    Long extractUserIdFromRequest(HttpServletRequest request);

}
