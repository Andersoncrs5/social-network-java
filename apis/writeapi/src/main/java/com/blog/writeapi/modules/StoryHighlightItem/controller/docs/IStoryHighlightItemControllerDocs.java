package com.blog.writeapi.modules.StoryHighlightItem.controller.docs;

import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.StoryHighlightItem.dto.CreateStoryHighlightItemDTO;
import com.blog.writeapi.utils.res.ResponseHttp;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

public interface IStoryHighlightItemControllerDocs {

    @PostMapping("/toggle")
    ResponseEntity<ResponseHttp<?>> create(
            @Valid @RequestBody CreateStoryHighlightItemDTO dto,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    );
}
