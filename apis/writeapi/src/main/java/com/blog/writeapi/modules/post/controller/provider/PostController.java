package com.blog.writeapi.modules.post.controller.provider;

import com.blog.writeapi.configs.api.idempotent.Idempotent;
import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.post.controller.docs.PostControllerDocs;
import com.blog.writeapi.modules.post.dtos.CreatePostDTO;
import com.blog.writeapi.modules.post.dtos.PostDTO;
import com.blog.writeapi.modules.post.dtos.UpdatePostDTO;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.post.services.interfaces.IPostService;
import com.blog.writeapi.utils.services.interfaces.ITokenService;
import com.blog.writeapi.modules.user.service.docs.IUserService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.post.isPostAuthor.IsPostAuthor;
import com.blog.writeapi.utils.mappers.PostMapper;
import com.blog.writeapi.utils.res.ResponseHttp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v1/post")
@RequiredArgsConstructor
@Validated
public class PostController implements PostControllerDocs {

    private final IPostService service;
    private final PostMapper mapper;

    @Override
    @Idempotent
    public ResponseEntity<?> create(
            @Valid @RequestBody CreatePostDTO dto,
            HttpServletRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    ) {
        PostModel model = this.service.create(dto, principal.getUser());

        PostDTO postMapped = this.mapper.toDTO(model);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseHttp<>(
                postMapped,
                "Post created with successfully",
                idempotencyKey,
                1,
                true,
                OffsetDateTime.now()
        ));
    }

    @Override
    public ResponseEntity<?> get(@PathVariable @IsId Long id, HttpServletRequest request) {
        PostModel post = this.service.getByIdSimple(id);

        ResponseHttp<PostDTO> res = new ResponseHttp<>(
                this.mapper.toDTO(post),
                "Post found",
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @Override
    @IsPostAuthor
    @Idempotent
    public ResponseEntity<?> del(
            @PathVariable @IsId Long id, HttpServletRequest request,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    ) {
        Optional<PostModel> post = this.service.getById(id);

        if (post.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseHttp<>(
                            null,
                            "Post not found",
                            idempotencyKey,
                            1,
                            false,
                            OffsetDateTime.now()
                    ));
        }

        this.service.delete(post.get());

        ResponseHttp<Object> res = new ResponseHttp<>(
                null,
                "Post deleted with successfully",
                idempotencyKey,
                1,
                true,
                OffsetDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @Override
    @IsPostAuthor
    @Idempotent
    public ResponseEntity<?> update(
            @PathVariable @IsId Long id,
            @Valid @RequestBody UpdatePostDTO dto,
            HttpServletRequest request,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    ) {
        PostModel post = this.service.getByIdSimple(id);

        PostModel postUpdated = this.service.update(dto, post);

        var postDto = this.mapper.toDTO(postUpdated);

        ResponseHttp<PostDTO> res = new ResponseHttp<>(
                postDto,
                "Post updated with successfully",
                idempotencyKey,
                1,
                true,
                OffsetDateTime.now()
        );

        return ResponseEntity.ok(res);
    }

}
