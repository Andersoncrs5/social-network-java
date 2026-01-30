package com.blog.writeapi.controllers.docs;

import com.blog.writeapi.dtos.postAttachment.CreatePostAttachmentDTO;
import com.blog.writeapi.dtos.postAttachment.UpdatePostAttachmentDTO;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface PostAttachmentControllerDocs {

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @CircuitBreaker(name = "tag-upload-file-cb")
    ResponseEntity<?> create(
            @Valid @RequestBody CreatePostAttachmentDTO dto,
            HttpServletRequest request
    );

    @DeleteMapping("{id}")
    @CircuitBreaker(name = "tagDeleteCB")
    ResponseEntity<?> delete(
            @PathVariable @IsId Long id,
            HttpServletRequest request
    );

    @PatchMapping("{id}")
    @CircuitBreaker(name = "tagUpdateCB")
    ResponseEntity<?> update(
            @PathVariable @IsId Long id,
            @RequestBody UpdatePostAttachmentDTO dto,
            HttpServletRequest request
    );
}
