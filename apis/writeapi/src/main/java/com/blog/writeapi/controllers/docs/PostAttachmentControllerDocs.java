package com.blog.writeapi.controllers.docs;

import com.blog.writeapi.dtos.postAttachment.CreatePostAttachmentDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface PostAttachmentControllerDocs {
    ResponseEntity<?> create(
            @Valid @RequestBody CreatePostAttachmentDTO dto,
            HttpServletRequest request
    );
}
