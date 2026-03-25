package com.blog.writeapi.modules.userBlock.controller.provider;

import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.userBlock.controller.docs.IUserBlockControllerDocs;
import com.blog.writeapi.modules.userBlock.model.UserBlockModel;
import com.blog.writeapi.modules.userBlock.service.docs.IUserBlockService;
import com.blog.writeapi.utils.classes.ResultToggle;
import com.blog.writeapi.utils.enums.global.ToggleEnum;
import com.blog.writeapi.utils.res.ResponseHttp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user-block")
public class UserBlockController implements IUserBlockControllerDocs {

    private final IUserBlockService service;

    @Override
    public ResponseEntity<?> toggle(
            @PathVariable Long blockerId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        ResultToggle<UserBlockModel> toggle = this.service.toggle(blockerId, principal.getId());

        String message = toggle.result() == ToggleEnum.ADDED
                ? "User blocked" : "User unblocked";

        HttpStatus status = toggle.result() == ToggleEnum.ADDED
                ? HttpStatus.CREATED : HttpStatus.OK;

        return ResponseEntity.status(status).body(new ResponseHttp<>(
                null,
                message,
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        ));

    }

}
