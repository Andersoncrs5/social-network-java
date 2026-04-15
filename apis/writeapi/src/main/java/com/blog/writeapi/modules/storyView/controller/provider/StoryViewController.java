package com.blog.writeapi.modules.storyView.controller.provider;

import com.blog.writeapi.configs.api.idempotent.Idempotent;
import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.storyView.controller.docs.IStoryViewControllerDocs;
import com.blog.writeapi.modules.storyView.service.interfaces.IStoryViewService;
import com.blog.writeapi.utils.res.ResponseHttp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("v1/story-view")
public class StoryViewController implements IStoryViewControllerDocs {

    private final IStoryViewService service;

    @Override
    @Idempotent
    public ResponseEntity<?> create(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    ) {
        boolean exists = service.createIfNotExists(principal.getId(), id);

        if (!exists) {
            return ResponseEntity
                    .status(200)
                    .body(ResponseHttp.success("Story view already exists", idempotencyKey));
        }

        return ResponseEntity
                .status(201)
                .body(ResponseHttp.success("Story view created", idempotencyKey));
    }

}
