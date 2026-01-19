package com.blog.writeapi.controllers.providers;

import com.blog.writeapi.controllers.docs.UserProfileControllerDocs;
import com.blog.writeapi.dtos.user.UserDTO;
import com.blog.writeapi.dtos.userProfile.UpdateUserProfileDTO;
import com.blog.writeapi.dtos.userProfile.UserProfileDTO;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.models.UserProfileModel;
import com.blog.writeapi.services.interfaces.ITokenService;
import com.blog.writeapi.services.interfaces.IUserProfileService;
import com.blog.writeapi.services.interfaces.IUserService;
import com.blog.writeapi.utils.mappers.UserProfileMapper;
import com.blog.writeapi.utils.res.ResponseHttp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user-profile")
public class UserProfileController implements UserProfileControllerDocs {

    private final IUserProfileService service;
    private final IUserService userService;
    private final ITokenService tokenService;
    private final UserProfileMapper mapper;

    @Override
    public ResponseEntity<?> update(
            @Valid @RequestBody UpdateUserProfileDTO dto,
            HttpServletRequest request
            ) {
        Long userID = this.tokenService.extractUserIdFromRequest(request);
        UserModel user = this.userService.GetByIdSimple(userID);
        UserProfileModel profile = this.service.getByUserSimple(user);

        UserProfileModel updated = this.service.update(profile, dto);

        ResponseHttp<UserProfileDTO> res = new ResponseHttp<>(
                mapper.toDTO(updated),
                "Profile updated",
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        );

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
