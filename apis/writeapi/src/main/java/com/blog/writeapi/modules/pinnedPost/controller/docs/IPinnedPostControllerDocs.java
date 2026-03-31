package com.blog.writeapi.modules.pinnedPost.controller.docs;

import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.pinnedPost.dto.CreatePinnedPostDTO;
import com.blog.writeapi.modules.pinnedPost.dto.UpdatePinnedPostDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

public interface IPinnedPostControllerDocs {
    @DeleteMapping("{id}")
    ResponseEntity<?> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    );

    @PostMapping
    ResponseEntity<?> create(
            @Valid @RequestBody CreatePinnedPostDTO dto,
            @AuthenticationPrincipal UserPrincipal principal
    );

    @PatchMapping("{id}")
    ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody UpdatePinnedPostDTO dto,
            @AuthenticationPrincipal UserPrincipal principal
    );
}
