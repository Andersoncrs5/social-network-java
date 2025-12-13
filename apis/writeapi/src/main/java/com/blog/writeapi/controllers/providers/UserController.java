package com.blog.writeapi.controllers.providers;

import com.blog.writeapi.controllers.docs.UserControllerDocs;
import com.blog.writeapi.dtos.user.UserDTO;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.services.interfaces.ITokenService;
import com.blog.writeapi.services.interfaces.IUserService;
import com.blog.writeapi.utils.mappers.UserMapper;
import com.blog.writeapi.utils.res.ResponseHttp;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserController implements UserControllerDocs {

    private final IUserService userService;
    private final ITokenService tokenService;
    private final UserMapper mapper;

    @Override
    public ResponseEntity<?> getUser(HttpServletRequest request) {
        Long userId = this.tokenService.extractUserIdFromRequest(request);

        log.debug("User id {}", userId );

        Optional<UserModel> opt = this.userService.GetById(userId);

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
