package com.blog.writeapi.modules.postReaction.controller.providers;

import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.postReaction.controller.docs.PostReactionControllerDocs;
import com.blog.writeapi.modules.postReaction.dtos.CreatePostReactionDTO;
import com.blog.writeapi.modules.postReaction.dtos.PostReactionDTO;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.postReaction.models.PostReactionModel;
import com.blog.writeapi.modules.postReaction.service.docs.IPostReactionService;
import com.blog.writeapi.modules.reaction.models.ReactionModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.post.services.interfaces.IPostService;
import com.blog.writeapi.modules.reaction.service.docs.IReactionService;
import com.blog.writeapi.modules.user.service.docs.IUserService;
import com.blog.writeapi.utils.mappers.PostReactionMapper;
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
    private final IReactionService reactionService;
    private final PostReactionMapper mapper;

    @Override
    public ResponseEntity<ResponseHttp<PostReactionDTO>> toggle(
            @Valid @RequestBody CreatePostReactionDTO dto,
            HttpServletRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        UserModel user = principal.getUser();
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
