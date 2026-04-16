package com.blog.writeapi.modules.commentView.controller.provider;

import com.blog.writeapi.configs.api.idempotent.Idempotent;
import com.blog.writeapi.configs.api.metadata.ClientMetadataDTO;
import com.blog.writeapi.configs.api.metadata.HttpRequestUtils;
import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.commentView.controller.docs.ICommentViewControllerDocs;
import com.blog.writeapi.modules.commentView.service.interfaces.ICommentViewService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.res.ResponseHttp;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/comment-view")
public class CommentViewController implements ICommentViewControllerDocs {

    private final ICommentViewService service;
    private final HttpRequestUtils httpRequest;

    @Override
    @Idempotent
    public ResponseEntity<?> create(
            @IsId @PathVariable Long commentId,
            HttpServletRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    ) {
        ClientMetadataDTO metadataDTO = this.httpRequest.extractMetadata(request);

        this.service.recordView(principal.getId(), commentId, metadataDTO);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                new ResponseHttp<>(
                    null,
                    "View added successfully",
                    idempotencyKey,
                    1,
                    true,
                    OffsetDateTime.now()
                )
            );
    }

}
