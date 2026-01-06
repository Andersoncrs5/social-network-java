package com.blog.writeapi.controllers.providers;

import com.blog.writeapi.controllers.docs.CommentFavoriteControllerDocs;
import com.blog.writeapi.models.CommentFavoriteModel;
import com.blog.writeapi.models.CommentModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.services.interfaces.ICommentFavoriteService;
import com.blog.writeapi.services.interfaces.ICommentService;
import com.blog.writeapi.services.interfaces.ITokenService;
import com.blog.writeapi.services.interfaces.IUserService;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.mappers.CommentFavoriteMapper;
import com.blog.writeapi.utils.res.ResponseHttp;
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
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/comment-favorite")
public class CommentFavoriteController implements CommentFavoriteControllerDocs {

    private final ICommentFavoriteService service;
    private final ITokenService tokenService;
    private final IUserService userService;
    private final ICommentService commentService;
    private final CommentFavoriteMapper mapper;

    @Override
    public ResponseEntity<?> toggle(
            @PathVariable @IsId Long commentID,
            HttpServletRequest request
    ) {
        Long userID = this.tokenService.extractUserIdFromRequest(request);

        UserModel user = this.userService.GetByIdSimple(userID);
        CommentModel comment = this.commentService.getByIdSimple(commentID);

        Optional<CommentFavoriteModel> favorite = this.service.findByCommentIdAndUserId(comment, user);

        if (favorite.isPresent()) {
            this.service.remove(favorite.get());

            return ResponseEntity.status(HttpStatus.OK).body(new ResponseHttp<>(
                    null,
                    "Comment removed from favorites successfully",
                    UUID.randomUUID().toString(),
                    1,
                    true,
                    OffsetDateTime.now()
            ));
        }

        CommentFavoriteModel add = this.service.add(user, comment);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseHttp<>(
                this.mapper.toDTO(add),
                "Comment added to favorites successfully",
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        ));
    }
}
