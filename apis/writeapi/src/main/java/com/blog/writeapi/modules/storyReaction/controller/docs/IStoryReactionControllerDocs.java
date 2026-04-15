package com.blog.writeapi.modules.storyReaction.controller.docs;

import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.storyReaction.dto.ToggleStoryReactionDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

public interface IStoryReactionControllerDocs {
    @PostMapping("/toggle")
    ResponseEntity<?> toggle(
            @RequestHeader("X-Idempotency-Key") String idempotencyKey,
            @RequestBody @Valid ToggleStoryReactionDTO dto,
            @AuthenticationPrincipal UserPrincipal principal
    );
}
