package com.blog.writeapi.modules.StoryHighlightItem.controller.provider;

import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.StoryHighlightItem.controller.docs.IStoryHighlightItemControllerDocs;
import com.blog.writeapi.modules.StoryHighlightItem.dto.CreateStoryHighlightItemDTO;
import com.blog.writeapi.modules.StoryHighlightItem.model.StoryHighlightItemModel;
import com.blog.writeapi.modules.StoryHighlightItem.service.interfaces.IStoryHighlightItemService;
import com.blog.writeapi.utils.classes.ResultToggle;
import com.blog.writeapi.utils.enums.global.ToggleEnum;
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
@RequestMapping("v1/story-highlight-item")
public class StoryHighlightItemController implements IStoryHighlightItemControllerDocs {

    private final IStoryHighlightItemService service;

    public ResponseEntity<ResponseHttp<?>> create(
            @Valid @RequestBody CreateStoryHighlightItemDTO dto,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    ) {
        ResultToggle<StoryHighlightItemModel> toggle = this.service.toggle(principal.getId(), dto);

        String message = toggle.result() == ToggleEnum.ADDED
                ? "Story added" : "Story removed";

        HttpStatus status = toggle.result() == ToggleEnum.ADDED
                ? HttpStatus.CREATED : HttpStatus.OK;

        return ResponseEntity.status(status).body(new ResponseHttp<>(
                null,
                message,
                idempotencyKey,
                1,
                true,
                OffsetDateTime.now()
        ));
    }


}
