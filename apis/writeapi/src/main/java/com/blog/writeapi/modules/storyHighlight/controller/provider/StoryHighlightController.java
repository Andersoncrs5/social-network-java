package com.blog.writeapi.modules.storyHighlight.controller.provider;

import com.blog.writeapi.configs.api.idempotent.Idempotent;
import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.storyHighlight.controller.docs.IStoryHighlightControllerDocs;
import com.blog.writeapi.modules.storyHighlight.dto.CreateStoryHighlightDTO;
import com.blog.writeapi.modules.storyHighlight.dto.StoryHighlightDTO;
import com.blog.writeapi.modules.storyHighlight.dto.UpdateStoryHighlightDTO;
import com.blog.writeapi.modules.storyHighlight.model.StoryHighlightModel;
import com.blog.writeapi.modules.storyHighlight.service.interfaces.IStoryHighlightService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.mappers.StoryHighlightMapper;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("v1/story-highlight")
public class StoryHighlightController implements IStoryHighlightControllerDocs {

    private final IStoryHighlightService service;
    private final StoryHighlightMapper mapper;

    @Idempotent
    @Override
    public ResponseEntity<?> create(
            @Valid @ModelAttribute CreateStoryHighlightDTO dto,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    ) {
        StoryHighlightModel model = this.service.create(principal.getId(), dto);

        return ResponseEntity
            .status(201)
            .body(
                ResponseHttp.success(
                    mapper.toDTO(model),
                    "Story highlight created",
                    idempotencyKey
                )
            );
    }

    @Idempotent
    @Override
    public ResponseEntity<?> delete(
            @PathVariable @IsId Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    ) {
        StoryHighlightModel model = this.service.findByIdSimple(id);
        boolean deleted = this.service.delete(model);

        if (!deleted) {
            return ResponseEntity
                .status(400)
                .body(
                    ResponseHttp.error(
                        "Error the delete story highlight",
                        idempotencyKey
                    )
                );
        }

        return ResponseEntity
            .status(200)
            .body(
                ResponseHttp.success(
                    "Story highlight deleted",
                    idempotencyKey
                )
            );
    }

    @Idempotent @Override
    public ResponseEntity<ResponseHttp<?>> patch(
            @PathVariable @IsId Long id,
            @Valid @ModelAttribute UpdateStoryHighlightDTO dto,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    ) {
        StoryHighlightModel model = this.service.findByIdSimple(id);
        Result<StoryHighlightModel> result = service.update(principal.getUser(), model, dto);

        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getStatus())
                    .body(ResponseHttp.error(result.getError().message(), idempotencyKey));
        }

        StoryHighlightDTO highlightDTO = this.mapper.toDTO(result.getValue());

        return ResponseEntity
                .status(result.getStatus())
                .body(ResponseHttp.success(highlightDTO, "Story highlight updated", idempotencyKey));
    }

}
