package com.blog.writeapi.modules.postView.controller.docs;

import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

public interface IPostViewControllerDocs {

    @PostMapping("/{postId}")
    @CircuitBreaker(name = "tagCreateCB")
    ResponseEntity<?> create(
            @IsId @PathVariable Long postId,
            HttpServletRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    );
}
