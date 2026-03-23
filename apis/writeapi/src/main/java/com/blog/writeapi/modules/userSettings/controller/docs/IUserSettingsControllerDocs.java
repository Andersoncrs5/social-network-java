package com.blog.writeapi.modules.userSettings.controller.docs;

import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.userSettings.dto.UpdateUserSettingsDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface IUserSettingsControllerDocs {

    @PatchMapping
    ResponseEntity<?> update(
            @RequestBody @Valid UpdateUserSettingsDTO dto,
            @AuthenticationPrincipal UserPrincipal principal
    );

}
