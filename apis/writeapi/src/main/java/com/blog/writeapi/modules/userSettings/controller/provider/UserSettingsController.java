package com.blog.writeapi.modules.userSettings.controller.provider;

import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.userSettings.controller.docs.IUserSettingsControllerDocs;
import com.blog.writeapi.modules.userSettings.dto.UpdateUserSettingsDTO;
import com.blog.writeapi.modules.userSettings.model.UserSettingsModel;
import com.blog.writeapi.modules.userSettings.service.interfaces.IUserSettingsService;
import com.blog.writeapi.utils.mappers.UserSettingsMapper;
import com.blog.writeapi.utils.res.ResponseHttp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user-settings")
public class UserSettingsController implements IUserSettingsControllerDocs {

    private final IUserSettingsService service;
    private final UserSettingsMapper mapper;

    @Override
    public ResponseEntity<?> update(
            @RequestBody UpdateUserSettingsDTO dto,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        UserSettingsModel setting = service.findByUserIdSimple(principal.getId());

        UserSettingsModel userSettingsUpdated = this.service.update(dto, setting);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                    new ResponseHttp<>(
                        mapper.toDTO(userSettingsUpdated),
                        "User setting updated",
                        UUID.randomUUID().toString(),
                        1,
                        true,
                        OffsetDateTime.now()
                    )
                );
    }

}
