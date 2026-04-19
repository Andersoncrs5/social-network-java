package com.blog.writeapi.modules.storyHighlight.controller.docs;

import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.storyHighlight.dto.CreateStoryHighlightDTO;
import com.blog.writeapi.modules.storyHighlight.dto.UpdateStoryHighlightDTO;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.res.ResponseHttp;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

public interface IStoryHighlightControllerDocs {
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @CircuitBreaker(name = "tag-upload-file-cb")
    ResponseEntity<?> create(
            @Valid @ModelAttribute CreateStoryHighlightDTO dto,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    );

    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(
            @PathVariable @IsId Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    );
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @CircuitBreaker(name = "tag-upload-file-cb")
    ResponseEntity<ResponseHttp<?>> patch(
            @PathVariable @IsId Long id,
            @Valid @ModelAttribute UpdateStoryHighlightDTO dto,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    );
}
