package com.blog.writeapi.modules.postReadingList.controller.docs;

import com.blog.writeapi.configs.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

public interface IPostReadingListControllerDocs {
    @PostMapping("toggle/{postId}")
    ResponseEntity<?> toggle(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserPrincipal principal
    );
}
