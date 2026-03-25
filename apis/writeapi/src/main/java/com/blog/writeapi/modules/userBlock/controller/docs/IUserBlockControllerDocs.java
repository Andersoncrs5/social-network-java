package com.blog.writeapi.modules.userBlock.controller.docs;

import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

public interface IUserBlockControllerDocs {
    @PostMapping("/{blockerId}/toggle")
    ResponseEntity<?> toggle(
            @PathVariable @IsId Long blockerId,
            @AuthenticationPrincipal UserPrincipal principal
    );
}
