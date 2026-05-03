package com.blog.writeapi.modules.userView.controller.docs;

import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

public interface IUserViewControllerDocs {

    @PostMapping("/{viewedId}")
    ResponseEntity<?> create(
            @PathVariable @IsId Long viewedId,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    );
}
