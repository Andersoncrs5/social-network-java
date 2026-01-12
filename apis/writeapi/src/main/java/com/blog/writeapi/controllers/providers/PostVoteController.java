package com.blog.writeapi.controllers.providers;

import com.blog.writeapi.controllers.docs.PostVoteControllerDocs;
import com.blog.writeapi.dtos.postVote.TogglePostVoteDTO;
import com.blog.writeapi.models.PostModel;
import com.blog.writeapi.models.PostVoteModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.services.interfaces.IPostService;
import com.blog.writeapi.services.interfaces.IPostVoteService;
import com.blog.writeapi.services.interfaces.ITokenService;
import com.blog.writeapi.services.interfaces.IUserService;
import com.blog.writeapi.utils.mappers.PostVoteMapper;
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
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/post-vote")
public class PostVoteController implements PostVoteControllerDocs {

    private final IPostVoteService service;
    private final IPostService postService;
    private final ITokenService tokenService;
    private final IUserService userService;
    private final PostVoteMapper mapper;

    @Override
    public ResponseEntity<?> toggle(
            @Valid @RequestBody TogglePostVoteDTO dto,
            HttpServletRequest request
    ) {
        Long userID = this.tokenService.extractUserIdFromRequest(request);

        PostModel post = this.postService.getByIdSimple(dto.postID());
        UserModel user = this.userService.GetByIdSimple(userID);

        Optional<PostVoteModel> voteOpt = this.service.findByUserAndPost(user, post);

        if (voteOpt.isPresent() && voteOpt.get().getType() == dto.type()) {
            this.service.delete(voteOpt.get());
            return ResponseEntity.ok(createResponse(null, "Vote removed successfully"));
        }

        PostVoteModel result;
        String message = "Vote applied successfully";
        HttpStatus status = HttpStatus.OK;

        if (voteOpt.isPresent()) {
            voteOpt.get().setType(dto.type());
            result = this.service.updateSimple(voteOpt.get());
        } else {
            result = this.service.create(dto, user, post);
            status = HttpStatus.CREATED;
        }

        return ResponseEntity.status(status).body(createResponse(this.mapper.toDTO(result), message));
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
