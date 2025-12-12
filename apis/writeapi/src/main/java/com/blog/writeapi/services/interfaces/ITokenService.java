package com.blog.writeapi.services.interfaces;

import com.blog.writeapi.models.RoleModel;
import com.blog.writeapi.models.UserModel;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

public interface ITokenService {
    String generateToken(UserModel user, List<RoleModel> roles);
    String validateToken(String token);
    String generateRefreshToken(UserModel user);
    Map<String, Object> extractAllClaims(String token);
    String extractSubjectFromRequest(HttpServletRequest request);
    String extractUserIdFromRequest(HttpServletRequest request);

}
