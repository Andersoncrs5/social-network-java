package com.blog.writeapi.modules.postView.controller.provider;

import com.blog.writeapi.configs.api.metadata.ClientMetadataDTO;
import com.blog.writeapi.configs.api.metadata.HttpRequestUtils;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.post.services.interfaces.IPostService;
import com.blog.writeapi.modules.postView.controller.docs.IPostViewControllerDocs;
import com.blog.writeapi.modules.postView.service.interfaces.IPostViewService;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.user.service.docs.IUserService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.services.interfaces.ITokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/post-view")
public class PostViewController implements IPostViewControllerDocs {

    private final IPostService postService;
    private final IPostViewService service;
    private final ITokenService tokenService;
    private final IUserService userService;
    private final HttpRequestUtils httpRequest;

    @Override
    public ResponseEntity<?> create(
            @IsId @PathVariable Long postId,
            HttpServletRequest request
    ) {
        Long userId = this.tokenService.extractUserIdFromRequest(request);
        ClientMetadataDTO metadataDTO = this.httpRequest.extractMetadata(request);

        PostModel post = this.postService.getByIdSimple(postId);
        UserModel user = this.userService.GetByIdSimple(userId);

        this.service.recordView(post, user, metadataDTO);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                new ResponseHttp<>(
                    null,
                    "View added successfully",
                    UUID.randomUUID().toString(),
                    1,
                    true,
                    OffsetDateTime.now()
                )
            );
    }

}
