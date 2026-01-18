package com.blog.writeapi.controllers.providers;

import com.blog.writeapi.controllers.docs.PostReactionControllerDocs;
import com.blog.writeapi.dtos.postReaction.CreatePostReactionDTO;
import com.blog.writeapi.dtos.postReaction.PostReactionDTO;
import com.blog.writeapi.models.PostModel;
import com.blog.writeapi.models.PostReactionModel;
import com.blog.writeapi.models.ReactionModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.services.interfaces.*;
import com.blog.writeapi.utils.mappers.PostReactionMapper;
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
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/post-reaction")
public class PostReactionController implements PostReactionControllerDocs {

    private final IPostReactionService service;
    private final IPostService postService;
    private final IUserService userService;
    private final ITokenService tokenService;
    private final IReactionService reactionService;
    private final PostReactionMapper mapper;

    @Override
    public ResponseEntity<ResponseHttp<PostReactionDTO>> toggle(
            @Valid @RequestBody CreatePostReactionDTO dto,
            HttpServletRequest request
    ) {
        Long userID = this.tokenService.extractUserIdFromRequest(request);

        UserModel user = this.userService.GetByIdSimple(userID);
        PostModel post = this.postService.getByIdSimple(dto.postId());
        ReactionModel reaction = this.reactionService.getByIdSimple(dto.reactionId());

        Optional<PostReactionModel> optional = this.service.findByPostAndUser(post, user);

        if (optional.isPresent() && Objects.equals(optional.get().getReaction().getId(), reaction.getId())) {
            this.service.delete(optional.get());
            return ResponseEntity.ok(createResponse(null, "Reaction removed successfully"));
        }

        if (optional.isPresent()) {
            optional.get().setReaction(reaction);
            PostReactionModel updated = this.service.updateSimple(optional.get());
            return ResponseEntity.ok(createResponse(this.mapper.toDTO(updated), "Reaction changed successfully"));
        }

        PostReactionModel model = this.service.create(post, reaction, user);
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
