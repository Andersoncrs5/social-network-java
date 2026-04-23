package com.blog.writeapi.modules.userTagPreference.controller.providers;

import com.blog.writeapi.configs.api.idempotent.Idempotent;
import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.userTagPreference.controller.docs.UserTagPreferenceControllerDocs;
import com.blog.writeapi.modules.userTagPreference.models.UserTagPreferenceModel;
import com.blog.writeapi.modules.userTagPreference.service.docs.IUserTagPreferenceService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.classes.ResultToggle;
import com.blog.writeapi.utils.enums.global.ToggleEnum;
import com.blog.writeapi.utils.mappers.UserTagPreferenceMapper;
import com.blog.writeapi.utils.res.ResponseHttp;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user-tag-preference")
public class UserTagPreferenceController implements UserTagPreferenceControllerDocs {

    private final IUserTagPreferenceService service;
    private final UserTagPreferenceMapper mapper;

    @Override
    @Idempotent
    public ResponseEntity<?> toggle(
            @PathVariable @IsId Long tagID,
            HttpServletRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    ) {
        ResultToggle<UserTagPreferenceModel> toggle = this.service.toggle(principal.getId(), tagID);

        String message = toggle.result() == ToggleEnum.ADDED
                ? "Tag added" : "Tag removed";

        HttpStatus status = toggle.result() == ToggleEnum.ADDED
                ? HttpStatus.CREATED : HttpStatus.OK;

        var dto = this.mapper.toDTO(toggle.body().orElse(null));

        return ResponseEntity.status(status).body(ResponseHttp.success(
                dto,
                message,
                idempotencyKey
        ));
    }

}
