package com.blog.writeapi.modules.commentReaction.controller.providers;

import com.blog.writeapi.configs.api.idempotent.Idempotent;
import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.comment.models.CommentModel;
import com.blog.writeapi.modules.comment.service.docs.ICommentService;
import com.blog.writeapi.modules.commentReaction.controller.docs.CommentReactionControllerDocs;
import com.blog.writeapi.modules.commentReaction.dtos.CreateCommentReactionDTO;
import com.blog.writeapi.modules.commentReaction.models.CommentReactionModel;
import com.blog.writeapi.modules.commentReaction.service.docs.ICommentReactionService;
import com.blog.writeapi.modules.reaction.models.ReactionModel;
import com.blog.writeapi.modules.reaction.service.docs.IReactionService;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.classes.ResultToggle;
import com.blog.writeapi.utils.enums.global.ToggleEnum;
import com.blog.writeapi.utils.mappers.CommentReactionMapper;
import com.blog.writeapi.utils.res.ResponseHttp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/comment-reaction")
public class CommentReactionController implements CommentReactionControllerDocs {

    private final CommentReactionMapper mapper;
    private final ICommentService commentService;
    private final IReactionService reactionService;
    private final ICommentReactionService service;

    @Override
    @Idempotent
    public ResponseEntity<?> toggle(
            @Valid @RequestBody CreateCommentReactionDTO dto,
            HttpServletRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    ) {
        UserModel user = principal.getUser();
        CommentModel comment = commentService.getByIdSimple(dto.commentID());
        ReactionModel reaction = this.reactionService.getByIdSimple(dto.reactionID());

        ResultToggle<CommentReactionModel> toggle = this.service.toggle(comment, reaction, user);

        String message = switch (toggle.result()) {
            case REMOVED -> "Reaction removed successfully";
            case UPDATED -> "Reaction changed successfully";
            case ADDED -> "Reaction applied successfully";
        };

        HttpStatus status = (toggle.result() == ToggleEnum.ADDED) ? HttpStatus.CREATED : HttpStatus.OK;
        var data = toggle.body().map(this.mapper::toDTO).orElse(null);

        return ResponseEntity.status(status)
                .body(ResponseHttp.success(data, message, idempotencyKey));
    }

}
