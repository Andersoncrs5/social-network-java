package com.blog.writeapi.controllers.providers;

import com.blog.writeapi.controllers.docs.PostControllerDocs;
import com.blog.writeapi.dtos.post.CreatePostDTO;
import com.blog.writeapi.dtos.post.PostDTO;
import com.blog.writeapi.models.PostModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.services.interfaces.IPostService;
import com.blog.writeapi.services.interfaces.ITokenService;
import com.blog.writeapi.services.interfaces.IUserService;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.mappers.PostMapper;
import com.blog.writeapi.utils.res.ResponseHttp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final ITokenService tokenService;
    private final IUserService userService;
    private final PostMapper mapper;

    @Override
    public ResponseEntity<?> create(@Valid @RequestBody CreatePostDTO dto, HttpServletRequest request) {
        Long userId = this.tokenService.extractUserIdFromRequest(request);

        Optional<UserModel> opt = this.userService.GetById(userId);
        if (opt.isEmpty()) {
            ResponseHttp<Object> res = new ResponseHttp<>(
                    null,
                    "User not found",
                    UUID.randomUUID().toString(),
                    1,
                    false,
                    OffsetDateTime.now()
            );

            return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
        }

        PostModel model = this.service.create(dto, opt.get());

        PostDTO postMapped = this.mapper.toDTO(model);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseHttp<>(
                postMapped,
                "Post created with successfully",
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        ));
    }

    @Override
    public ResponseEntity<?> get(@PathVariable @IsId Long id, HttpServletRequest request) {
        Optional<PostModel> post = this.service.getById(id);

        if (post.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseHttp<>(
                            null,
                            "Post not found",
                            UUID.randomUUID().toString(),
                            1,
                            false,
                            OffsetDateTime.now()
                            ));
        }

        ResponseHttp<PostDTO> res = new ResponseHttp<>(
                this.mapper.toDTO(post.get()),
                "Post found",
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @Override
    public ResponseEntity<?> del(@PathVariable @IsId Long id, HttpServletRequest request) {
        Long userId = this.tokenService.extractUserIdFromRequest(request);

        Optional<PostModel> post = this.service.getById(id);

        if (post.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseHttp<>(
                            null,
                            "Post not found",
                            UUID.randomUUID().toString(),
                            1,
                            false,
                            OffsetDateTime.now()
                    ));
        }

        if (!post.get().getAuthor().getId().equals(userId)) {
            ResponseHttp<Object> res = new ResponseHttp<>(
                    null,
                    "Only the author can delete this post",
                    UUID.randomUUID().toString(),
                    1,
                    false,
                    OffsetDateTime.now()
            );

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(res);
        }

        this.service.delete(post.get());

        ResponseHttp<Object> res = new ResponseHttp<>(
                null,
                "Post deleted with successfully",
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

}
