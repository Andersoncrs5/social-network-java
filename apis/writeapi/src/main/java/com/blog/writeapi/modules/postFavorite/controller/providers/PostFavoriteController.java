package com.blog.writeapi.modules.postFavorite.controller.providers;

import com.blog.writeapi.configs.api.idempotent.Idempotent;
import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.post.services.interfaces.IPostService;
import com.blog.writeapi.modules.postFavorite.controller.docs.PostFavoriteControllerDocs;
import com.blog.writeapi.modules.postFavorite.models.PostFavoriteModel;
import com.blog.writeapi.modules.postFavorite.service.docs.IPostFavoriteService;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.classes.ResultToggle;
import com.blog.writeapi.utils.enums.global.ToggleEnum;
import com.blog.writeapi.utils.mappers.PostFavoriteMapper;
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

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/post-favorite")
public class PostFavoriteController implements PostFavoriteControllerDocs {

    private final IPostFavoriteService service;
    private final IPostService iPostService;
    private final PostFavoriteMapper mapper;

    @Override
    @Idempotent
    public ResponseEntity<?> toggle(
            @PathVariable @IsId Long postId,
            HttpServletRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    ) {
        UserModel user = principal.getUser();
        PostModel post = this.iPostService.getByIdSimple(postId);

        ResultToggle<PostFavoriteModel> toggle = this.service.toggle(post, user);

        String message = (toggle.result() == ToggleEnum.ADDED)
                ? "Post added to favorites successfully"
                : "Post removed from favorites successfully";

        HttpStatus status = (toggle.result() == ToggleEnum.ADDED)
                ? HttpStatus.CREATED
                : HttpStatus.OK;

        Object data = toggle.body().map(this.mapper::toDTO).orElse(null);

        ResponseHttp<Object> response = new ResponseHttp<>(
                data,
                message,
                idempotencyKey,
                1,
                true,
                OffsetDateTime.now()
        );

        return ResponseEntity.status(status).body(response);
    }


}
