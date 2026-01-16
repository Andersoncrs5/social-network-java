package com.blog.writeapi.controllers.providers;

import com.blog.writeapi.controllers.docs.CommentVoteControllerDocs;
import com.blog.writeapi.dtos.commentVote.ToggleCommentVoteDTO;
import com.blog.writeapi.models.CommentModel;
import com.blog.writeapi.models.CommentVoteModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.services.interfaces.ICommentService;
import com.blog.writeapi.services.interfaces.ICommentVoteService;
import com.blog.writeapi.services.interfaces.ITokenService;
import com.blog.writeapi.services.interfaces.IUserService;
import com.blog.writeapi.utils.mappers.CommentVoteMapper;
import com.blog.writeapi.utils.res.ResponseHttp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
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
    private final IUserService userService;
    private final ICommentService commentService;
    private final ITokenService tokenService;
    private final CommentVoteMapper mapper;

    @Override
    public ResponseEntity<?> toggle(
            @Valid @RequestBody ToggleCommentVoteDTO dto,
            HttpServletRequest request
    ) {
        Long userID = this.tokenService.extractUserIdFromRequest(request);
        UserModel user = this.userService.GetByIdSimple(userID);
        CommentModel comment = this.commentService.getByIdSimple(dto.commentID());

        Optional<CommentVoteModel> voteOpt = this.service.findByUserAndComment(user, comment);

        if (voteOpt.isPresent() && voteOpt.get().getType() == dto.type()) {
            this.service.delete(voteOpt.get());
            return ResponseEntity.ok(createResponse(null, "Comment vote removed successfully"));
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
                createResponse(this.mapper.toDTO(result), message)
        );
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