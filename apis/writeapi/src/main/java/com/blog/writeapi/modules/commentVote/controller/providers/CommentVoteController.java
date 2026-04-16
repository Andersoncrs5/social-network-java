package com.blog.writeapi.modules.commentVote.controller.providers;

import com.blog.writeapi.configs.api.idempotent.Idempotent;
import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.commentVote.controller.docs.CommentVoteControllerDocs;
import com.blog.writeapi.modules.commentVote.dtos.ToggleCommentVoteDTO;
import com.blog.writeapi.modules.comment.models.CommentModel;
import com.blog.writeapi.modules.commentVote.models.CommentVoteModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.comment.service.docs.ICommentService;
import com.blog.writeapi.modules.commentVote.service.docs.ICommentVoteService;
import com.blog.writeapi.utils.services.interfaces.ITokenService;
import com.blog.writeapi.modules.user.service.docs.IUserService;
import com.blog.writeapi.utils.mappers.CommentVoteMapper;
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

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v1/comment-vote")
@RequiredArgsConstructor
@Validated
public class CommentVoteController implements CommentVoteControllerDocs {

    private final ICommentVoteService service;
    private final ICommentService commentService;
    private final CommentVoteMapper mapper;

    @Override
    @Idempotent
    public ResponseEntity<?> toggle(
            @Valid @RequestBody ToggleCommentVoteDTO dto,
            HttpServletRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    ) {
        UserModel user = principal.getUser();
        CommentModel comment = this.commentService.getByIdSimple(dto.commentID());

        Optional<CommentVoteModel> voteOpt = this.service.findByUserAndComment(user, comment);

        if (voteOpt.isPresent() && voteOpt.get().getType() == dto.type()) {
            this.service.delete(voteOpt.get());
            return ResponseEntity.ok(createResponse(null, "Comment vote removed successfully", idempotencyKey));
        }

        CommentVoteModel result;
        String message = "Comment vote applied successfully";
        HttpStatus status = HttpStatus.OK;

        if (voteOpt.isPresent()) {
            voteOpt.get().setType(dto.type());
            result = this.service.updateSimple(voteOpt.get());
        }
        else {
            result = this.service.create(dto, comment, user);
            status = HttpStatus.CREATED;
        }

        return ResponseEntity.status(status).body(
                createResponse(this.mapper.toDTO(result), message, idempotencyKey)
        );
    }

    private <T> ResponseHttp<T> createResponse(T data, String message, String traceID) {
        return new ResponseHttp<>(
                data,
                message,
                traceID,
                1,
                true,
                OffsetDateTime.now()
        );
    }
}