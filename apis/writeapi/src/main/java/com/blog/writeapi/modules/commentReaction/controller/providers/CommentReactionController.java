package com.blog.writeapi.modules.commentReaction.controller.providers;

import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.commentReaction.controller.docs.CommentReactionControllerDocs;
import com.blog.writeapi.modules.commentReaction.dtos.CreateCommentReactionDTO;
import com.blog.writeapi.modules.comment.models.CommentModel;
import com.blog.writeapi.modules.comment.service.docs.ICommentService;
import com.blog.writeapi.modules.commentReaction.models.CommentReactionModel;
import com.blog.writeapi.modules.commentReaction.service.docs.ICommentReactionService;
import com.blog.writeapi.modules.reaction.models.ReactionModel;
import com.blog.writeapi.modules.reaction.service.docs.IReactionService;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.user.service.docs.IUserService;
import com.blog.writeapi.utils.mappers.CommentReactionMapper;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.services.interfaces.ITokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/comment-reaction")
public class CommentReactionController implements CommentReactionControllerDocs {

    private final ITokenService tokenService;
    private final CommentReactionMapper mapper;
    private final IUserService userService;
    private final ICommentService commentService;
    private final IReactionService reactionService;
    private final ICommentReactionService service;

    public ResponseEntity<?> toggle(
            @Valid @RequestBody CreateCommentReactionDTO dto,
            HttpServletRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        UserModel user = principal.getUser();
        CommentModel comment = commentService.getByIdSimple(dto.commentID());
        ReactionModel reaction = this.reactionService.getByIdSimple(dto.reactionID());

        var optional = this.service.findByUserAndComment(user, comment);

        if (optional.isPresent() && Objects.equals(optional.get().getReaction().getId(), reaction.getId())) {
            this.service.delete(optional.get());
            return ResponseEntity.ok(createResponse(null, "Reaction removed successfully"));
        }

        if (optional.isPresent()) {
            optional.get().setReaction(reaction);
            var updated = this.service.updateSimple(optional.get());
            return ResponseEntity.ok(createResponse(this.mapper.toDTO(updated), "Reaction changed successfully"));
        }

        CommentReactionModel model = this.service.create(comment, reaction, user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createResponse(this.mapper.toDTO(model), "Reaction applied successfully"));
    }

    private <T> ResponseHttp<T> createResponse(T data, String message) {
        return new ResponseHttp<>(
                data,
                message,
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        );
    }
}
