package com.blog.writeapi.modules.postAttachment.controller.docs;

import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.postAttachment.dtos.CreatePostAttachmentDTO;
import com.blog.writeapi.modules.postAttachment.dtos.UpdatePostAttachmentDTO;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

public interface PostAttachmentControllerDocs {

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @CircuitBreaker(name = "tag-upload-file-cb")
    ResponseEntity<?> create(
            @Valid @RequestBody CreatePostAttachmentDTO dto,
            HttpServletRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    );

    @DeleteMapping("{id}")
    @CircuitBreaker(name = "tagDeleteCB")
    ResponseEntity<?> delete(
            @PathVariable @IsId Long id,
            HttpServletRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    );

    @PatchMapping("{id}")
    @CircuitBreaker(name = "tagUpdateCB")
    ResponseEntity<?> update(
            @PathVariable @IsId Long id,
            @RequestBody UpdatePostAttachmentDTO dto,
            HttpServletRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    );
}
