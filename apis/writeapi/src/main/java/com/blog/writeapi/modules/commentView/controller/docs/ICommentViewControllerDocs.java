package com.blog.writeapi.modules.commentView.controller.docs;

import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

public interface ICommentViewControllerDocs {

    @PostMapping("/{commentId}")
    ResponseEntity<?> create(
            @IsId @PathVariable Long commentId,
            HttpServletRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    );
}
