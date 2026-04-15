package com.blog.writeapi.modules.storyReaction.controller.provider;

import com.blog.writeapi.configs.api.idempotent.Idempotent;
import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.storyReaction.controller.docs.IStoryReactionControllerDocs;
import com.blog.writeapi.modules.storyReaction.dto.StoryReactionDTO;
import com.blog.writeapi.modules.storyReaction.dto.ToggleStoryReactionDTO;
import com.blog.writeapi.modules.storyReaction.model.StoryReactionModel;
import com.blog.writeapi.modules.storyReaction.service.interfaces.IStoryReactionService;
import com.blog.writeapi.utils.classes.ResultToggle;
import com.blog.writeapi.utils.mappers.StoryReactionMapper;
import com.blog.writeapi.utils.res.ResponseHttp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("v1/story-reaction")
public class StoryReactionController implements IStoryReactionControllerDocs {

    private final IStoryReactionService service;
    private final StoryReactionMapper mapper;

    @Override
    @Idempotent
    public ResponseEntity<?> toggle(
            @RequestHeader("X-Idempotency-Key") String idempotencyKey,
            @RequestBody @Valid ToggleStoryReactionDTO dto,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        ResultToggle<StoryReactionModel> toggle = this.service
                .react1(principal.getId(), dto.storyId(), dto.reactionId());

        String message = "";
        HttpStatus status = HttpStatus.OK;
        StoryReactionDTO storyReactionDTO = null;

        switch (toggle.result()) {
            case ADDED -> {
                message = "Reaction added";
                status = HttpStatus.CREATED;
                storyReactionDTO = toggle.body().map(this.mapper::toDTO).orElse(null);
            }
            case REMOVED -> {
                message = "Reaction removed";
            }
            case UPDATED -> {
                message = "Reaction updated";
                storyReactionDTO = toggle.body().map(this.mapper::toDTO).orElse(null);
            }
        }

        return ResponseEntity.status(status).body(new ResponseHttp<>(
                storyReactionDTO,
                message,
                idempotencyKey,
                1,
                true,
                OffsetDateTime.now()
        ));

    }

}
