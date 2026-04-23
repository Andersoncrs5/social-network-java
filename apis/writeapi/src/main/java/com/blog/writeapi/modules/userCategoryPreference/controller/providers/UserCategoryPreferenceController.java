package com.blog.writeapi.modules.userCategoryPreference.controller.providers;

import com.blog.writeapi.configs.api.idempotent.Idempotent;
import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.userCategoryPreference.controller.docs.UserCategoryPreferenceControllerDocs;
import com.blog.writeapi.modules.userCategoryPreference.dtos.UserCategoryPreferenceDTO;
import com.blog.writeapi.modules.userCategoryPreference.models.UserCategoryPreferenceModel;
import com.blog.writeapi.modules.userCategoryPreference.service.docs.IUserCategoryPreferenceService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.classes.ResultToggle;
import com.blog.writeapi.utils.enums.global.ToggleEnum;
import com.blog.writeapi.utils.mappers.UserCategoryPreferenceMapper;
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
@RequestMapping("/v1/user-category-preference")
public class UserCategoryPreferenceController implements UserCategoryPreferenceControllerDocs {

    private final IUserCategoryPreferenceService service;
    private final UserCategoryPreferenceMapper mapper;

    @Override
    @Idempotent
    public ResponseEntity<?> toggle(
            @PathVariable @IsId Long categoryId,
            HttpServletRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    ) {
        ResultToggle<UserCategoryPreferenceModel> toggle = this.service.toggle(categoryId, principal.getId());

        String message = toggle.result() == ToggleEnum.ADDED
                ? "Category added" : "Category removed";

        HttpStatus status = toggle.result() == ToggleEnum.ADDED
                ? HttpStatus.CREATED : HttpStatus.OK;

        UserCategoryPreferenceDTO dto = this.mapper.toDTO(toggle.body().orElse(null));

        return ResponseEntity.status(status).body(ResponseHttp.success(
            dto,
            message,
            idempotencyKey
        ));
    }

}
