package com.blog.writeapi.modules.user.controller.providers;

import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.user.controller.docs.UserControllerDocs;
import com.blog.writeapi.modules.user.dtos.UpdateUserDTO;
import com.blog.writeapi.modules.user.dtos.UserDTO;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.services.interfaces.ITokenService;
import com.blog.writeapi.modules.user.service.docs.IUserService;
import com.blog.writeapi.utils.mappers.UserMapper;
import com.blog.writeapi.utils.res.ResponseHttp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserController implements UserControllerDocs {

    private final IUserService userService;
    private final UserMapper mapper;

    @Override
    public ResponseEntity<?> getUser(
            HttpServletRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        UserModel opt = principal.getUser();

        UserDTO userDTO = this.mapper.toDTO(opt);

        ResponseHttp<UserDTO> res = new ResponseHttp<>(
                userDTO,
                "User found",
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        );

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> deleteUser(
            HttpServletRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        UserModel opt = principal.getUser();

        this.userService.Delete(opt);

        ResponseHttp<Object> res = new ResponseHttp<>(
                null,
                "User deleted",
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        );

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> update(
            @Valid @RequestBody UpdateUserDTO dto,
            HttpServletRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        UserModel currentUser = principal.getUser();

        if (dto.username() != null && !dto.username().isBlank()) {
            String newUsername = dto.username();

            if (!Objects.equals(currentUser.getUsername(), newUsername)) {

                Boolean usernameAlreadyExists = this.userService.existsByUsername(newUsername);
                if (usernameAlreadyExists) {
                    ResponseHttp<Object> res = new ResponseHttp<>(
                            null,
                            "The Username '" + newUsername + "' is already in use by another user.",
                            UUID.randomUUID().toString(),
                            1,
                            false,
                            OffsetDateTime.now()
                    );
                    return new ResponseEntity<>(res, HttpStatus.CONFLICT);
                }
            }
        }

        UserModel user = this.userService.Update(dto, currentUser);

        UserDTO userDTO = this.mapper.toDTO(user);

        ResponseHttp<UserDTO> res = new ResponseHttp<>(
                userDTO,
                "User update",
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        );

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getUser(
            @PathVariable @IsId Long id,
            HttpServletRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        Optional<UserModel> opt = this.userService.GetById(id);

        if (opt.isEmpty()) {
            ResponseHttp<Object> res = new ResponseHttp<>(
                    null,
                    "User not found",
                    UUID.randomUUID().toString(),
                    1,
                    false,
                    OffsetDateTime.now()
            );

            return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
        }

        UserDTO userDTO = this.mapper.toDTO(opt.get());

        ResponseHttp<UserDTO> res = new ResponseHttp<>(
                userDTO,
                "User found",
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        );

        return new ResponseEntity<>(res, HttpStatus.OK);
    }


}
