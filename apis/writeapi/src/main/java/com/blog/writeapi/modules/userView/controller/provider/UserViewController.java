package com.blog.writeapi.modules.userView.controller.provider;

import com.blog.writeapi.configs.api.idempotent.Idempotent;
import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.userView.controller.docs.IUserViewControllerDocs;
import com.blog.writeapi.modules.userView.model.UserViewModel;
import com.blog.writeapi.modules.userView.service.interfaces.IUserViewService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.result.Result;
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
@RequestMapping("/v1/user-view")
@RequiredArgsConstructor
public class UserViewController implements IUserViewControllerDocs {

    private final IUserViewService service;

    @Override @Idempotent
    public ResponseEntity<?> create(
            @PathVariable @IsId Long viewedId,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    ) {
        Result<UserViewModel> result = this.service.createIfNotExists(principal.getId(), viewedId);

        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getStatus())
                    .body(ResponseHttp.error(result.getMessage(), idempotencyKey));
        }

        String message = result.getStatus() == HttpStatus.CREATED ? "User view created" : "User view already exists";

        return ResponseEntity
                .status(result.getStatus())
                .body(ResponseHttp.success(message, idempotencyKey));
    }

}
