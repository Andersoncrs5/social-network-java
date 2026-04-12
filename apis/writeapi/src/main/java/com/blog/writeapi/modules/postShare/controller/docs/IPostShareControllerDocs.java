package com.blog.writeapi.modules.postShare.controller.docs;

import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.postShare.dto.CreatePostShareDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface IPostShareControllerDocs {
    @PostMapping
    ResponseEntity<?> create(
            @RequestBody @Valid CreatePostShareDTO dto,
            @AuthenticationPrincipal UserPrincipal principal
    );
}
