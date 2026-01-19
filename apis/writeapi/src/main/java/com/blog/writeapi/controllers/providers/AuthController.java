package com.blog.writeapi.controllers.providers;

import com.blog.writeapi.controllers.docs.AuthControllerDocs;
import com.blog.writeapi.dtos.user.CreateUserDTO;
import com.blog.writeapi.dtos.user.LoginUserDTO;
import com.blog.writeapi.models.RoleModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.models.UserRoleModel;
import com.blog.writeapi.services.interfaces.*;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.res.ResponseTokens;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController implements AuthControllerDocs {

    private final IUserService userService;
    private final IRoleService roleService;
    private final IUserRoleService userRoleService;
    private final ITokenService tokenService;
    private final Argon2PasswordEncoder encoder;
    private final IUserProfileService iUserProfileService;

    @Override
    public ResponseEntity<?> login(@Valid @RequestBody LoginUserDTO dto, HttpServletRequest request) {

        Optional<UserModel> optional = this.userService.findByEmail(dto.email());
        if (optional.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        UserModel user = optional.get();

        boolean matches = this.encoder.matches(dto.password(), user.getPassword());
        if (!matches) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<RoleModel> roles = user.getRoles().stream().map(UserRoleModel::getRole).toList();

        String token = this.tokenService.generateToken(user, roles);
        String refreshToken = this.tokenService.generateRefreshToken(user);

        user.setRefreshToken(refreshToken);

        this.userService.UpdateSimple(user);

        ResponseTokens tokens = new ResponseTokens(
                token,
                refreshToken
        );

        ResponseHttp<ResponseTokens> response = new ResponseHttp<>(
                tokens,
                "Welcome again",
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    public ResponseEntity<?> Create(@Valid @RequestBody CreateUserDTO dto, HttpServletRequest request) {
        UserModel user = this.userService.Create(dto);

        Optional<RoleModel> roleOpt = this.roleService.findByName("USER_ROLE");

        if (roleOpt.isEmpty()) throw new RuntimeException("Role USER_ROLE not exists");

        this.userRoleService.create(user, roleOpt.get());

        String token = this.tokenService.generateToken(user, List.of(roleOpt.get()));
        String refreshToken = this.tokenService.generateRefreshToken(user);

        user.setRefreshToken(refreshToken);

        UserModel updated = this.userService.UpdateSimple(user);
        this.iUserProfileService.create(updated);

        ResponseTokens tokens = new ResponseTokens(
                token,
                refreshToken
        );

        ResponseHttp<ResponseTokens> response = new ResponseHttp<>(
                tokens,
        "Welcome",
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
