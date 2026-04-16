package com.blog.writeapi.modules.commentFavorite.controller.providers;

import com.blog.writeapi.configs.api.idempotent.Idempotent;
import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.comment.models.CommentModel;
import com.blog.writeapi.modules.comment.service.docs.ICommentService;
import com.blog.writeapi.modules.commentFavorite.controller.docs.CommentFavoriteControllerDocs;
import com.blog.writeapi.modules.commentFavorite.models.CommentFavoriteModel;
import com.blog.writeapi.modules.commentFavorite.service.docs.ICommentFavoriteService;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.mappers.CommentFavoriteMapper;
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
import java.util.Optional;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/comment-favorite")
public class CommentFavoriteController implements CommentFavoriteControllerDocs {

    private final ICommentFavoriteService service;
    private final ICommentService commentService;
    private final CommentFavoriteMapper mapper;

    @Override
    @Idempotent
    public ResponseEntity<?> toggle(
            @RequestHeader("X-Idempotency-Key") String idempotencyKey,
            @PathVariable @IsId Long commentID,
            HttpServletRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        UserModel user = principal.getUser();
        CommentModel comment = this.commentService.getByIdSimple(commentID);

        Optional<CommentFavoriteModel> favorite = this.service.findByCommentIdAndUserId(comment, user);

        if (favorite.isPresent()) {
            this.service.remove(favorite.get());

            return ResponseEntity.status(HttpStatus.OK).body(new ResponseHttp<>(
                    null,
                    "Comment removed from favorites successfully",
                    idempotencyKey,
                    1,
                    true,
                    OffsetDateTime.now()
            ));
        }

        CommentFavoriteModel add = this.service.add(user, comment);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseHttp<>(
                this.mapper.toDTO(add),
                "Comment added to favorites successfully",
                idempotencyKey,
                1,
                true,
                OffsetDateTime.now()
        ));
    }
}
