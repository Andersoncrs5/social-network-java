package com.blog.writeapi.modules.adm.controller.providers;

import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.adm.controller.docs.AdmControllerDocs;
import com.blog.writeapi.modules.adm.dto.ToggleRoleAdmDTO;
import com.blog.writeapi.modules.adm.dto.ToggleRoleDTO;
import com.blog.writeapi.modules.adm.service.docs.IAdmService;
import com.blog.writeapi.modules.userRole.models.UserRoleModel;
import com.blog.writeapi.utils.classes.ResultToggle;
import com.blog.writeapi.utils.enums.global.ToggleEnum;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Objects;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/adm")
public class AdmController implements AdmControllerDocs {

    private final IAdmService service;

    @Override
    public ResponseEntity<?> addRoleToUser(
            @RequestBody @Valid ToggleRoleDTO dto,
            HttpServletRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    ) {
        if (Objects.equals(dto.userId(), principal.getId())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ResponseHttp.error(
                        "You cannot toggle your own administrative role." , idempotencyKey)
                    );
        }

        Result<UserRoleModel> result = this.service.addRoleToUser(dto);

        if (result.isFailure())
        {
            return ResponseEntity
                .status(result.getStatus())
                .body(
                    ResponseHttp
                        .error(
                            result.getError().message(), idempotencyKey
                        )
                );
        }

        return ResponseEntity
            .status(result.getStatus())
            .body(
                ResponseHttp
                    .success(
                        "Role added with successfully", idempotencyKey
                    )
            );
    }

    @Override
    public ResponseEntity<ResponseHttp<?>> removeRoleToUser(
            @RequestBody @Valid ToggleRoleDTO dto,
            HttpServletRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    ) {
        if (Objects.equals(dto.userId(), principal.getId())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ResponseHttp.error(
                            "You cannot toggle your own administrative role." , idempotencyKey)
                    );
        }

        Result<Void> result = this.service.removeRoleToUser(dto);

        if (result.isFailure())
        {
            return ResponseEntity
                .status(result.getStatus())
                .body(
                    ResponseHttp
                        .error(
                            result.getError().message(), idempotencyKey
                        )
                );
        }

        return ResponseEntity
            .status(result.getStatus())
            .body(
                ResponseHttp
                    .success(
                            "Role added with successfully", idempotencyKey
                    )
            );
    }

    @Override
    public ResponseEntity<ResponseHttp<Object>> toggleRoleAdmInUser(
            @RequestBody @Valid ToggleRoleAdmDTO dto,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    ) {

        if (Objects.equals(principal.getId(), dto.userId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseHttp<>(
                    null,
                    "You cannot toggle your own administrative role." ,
                    idempotencyKey,
                    1,
                    false,
                    OffsetDateTime.now()
            ));
        }

        ResultToggle<UserRoleModel> result = this.service.toggleRoleAdmInUser(
                new ToggleRoleDTO(
                        "ADM_ROLE",
                        dto.userId()
                ),
                principal.getId()
        );

        if (result.result().equals(ToggleEnum.REMOVED))
        {
            return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                    ResponseHttp
                        .success(
                            null,"Role 'ADM_ROLE' was removed", idempotencyKey
                        )
                );
        }

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                ResponseHttp
                    .success(
                            null, "Role 'ADM_ROLE' was added", idempotencyKey
                    )
            );
    }


}
