package com.blog.writeapi.modules.userProfile.controller.providers;

import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.userProfile.controller.docs.UserProfileControllerDocs;
import com.blog.writeapi.modules.userProfile.dtos.UpdateUserProfileDTO;
import com.blog.writeapi.modules.userProfile.dtos.UserProfileDTO;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.userProfile.models.UserProfileModel;
import com.blog.writeapi.utils.services.interfaces.ITokenService;
import com.blog.writeapi.modules.userProfile.service.docs.IUserProfileService;
import com.blog.writeapi.modules.user.service.docs.IUserService;
import com.blog.writeapi.utils.mappers.UserProfileMapper;
import com.blog.writeapi.utils.res.ResponseHttp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final UserProfileMapper mapper;

    @Override
    public ResponseEntity<?> update(
            @Valid @RequestBody UpdateUserProfileDTO dto,
            HttpServletRequest request,
            @AuthenticationPrincipal UserPrincipal principal
            ) {
        UserModel user = principal.getUser();
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
