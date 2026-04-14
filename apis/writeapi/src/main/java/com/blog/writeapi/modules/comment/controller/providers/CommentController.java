package com.blog.writeapi.modules.comment.controller.providers;

import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.comment.controller.docs.CommentControllerDocs;
import com.blog.writeapi.modules.comment.dtos.CommentDTO;
import com.blog.writeapi.modules.comment.dtos.CreateCommentDTO;
import com.blog.writeapi.modules.comment.dtos.UpdateCommentDTO;
import com.blog.writeapi.modules.comment.models.CommentModel;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.comment.service.docs.ICommentService;
import com.blog.writeapi.modules.post.services.interfaces.IPostService;
import com.blog.writeapi.utils.services.interfaces.ITokenService;
import com.blog.writeapi.modules.user.service.docs.IUserService;
import com.blog.writeapi.utils.annotations.validations.comment.isAuthorComment.IsAuthorComment;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.mappers.CommentMapper;
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
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/comment")
public class CommentController implements CommentControllerDocs {

    private final ICommentService service;
    private final IPostService postService;
    private final CommentMapper mapper;

    @Override
    @IsAuthorComment
    public ResponseEntity<?> del(
            @PathVariable @IsId Long id,
            HttpServletRequest request,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    ) {
        CommentModel comment = this.service.getByIdSimple(id);

        this.service.delete(comment);

        ResponseHttp<Object> res = new ResponseHttp<>(
            null,
                "Comment deleted with successfully",
                idempotencyKey,
                1,
                true,
                OffsetDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @Override
    public ResponseEntity<?> create(
            @Valid @RequestBody CreateCommentDTO dto,
            HttpServletRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    ) {
        CommentModel parent = null;
        PostModel post = this.postService.getByIdSimple(dto.postID());
        UserModel user = principal.getUser();

        if (dto.parentId() != null) {
            parent = this.service.getByIdSimple(dto.parentId());
        }

        CommentModel commentCreated = this.service.create(dto, post, user, parent);

        ResponseHttp<CommentDTO> res = new ResponseHttp<>(
                this.mapper.toDTO(commentCreated),
                "Comment created with successfully",
                idempotencyKey,
                1,
                true,
                OffsetDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @Override
    @IsAuthorComment
    public ResponseEntity<?> update(
            @PathVariable @IsId Long id,
            @Valid @RequestBody UpdateCommentDTO dto,
            HttpServletRequest request,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
            ) {
        CommentModel comment = this.service.getByIdSimple(id);

        CommentModel update = this.service.update(dto, comment);

        ResponseHttp<CommentDTO> res = new ResponseHttp<>(
                this.mapper.toDTO(update),
                "Comment updated with successfully",
                idempotencyKey,
                1,
                true,
                OffsetDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

}
