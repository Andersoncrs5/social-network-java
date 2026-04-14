package com.blog.writeapi.modules.stories.controller.provider;

import com.blog.writeapi.configs.api.idempotent.Idempotent;
import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.stories.controller.docs.IStoryControllerDocs;
import com.blog.writeapi.modules.stories.dto.CreateStoryDTO;
import com.blog.writeapi.modules.stories.dto.StoryDTO;
import com.blog.writeapi.modules.stories.model.StoryModel;
import com.blog.writeapi.modules.stories.service.interfaces.IStoryService;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.mappers.StoryMapper;
import com.blog.writeapi.utils.res.ResponseHttp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("v1/story")
public class StoryController implements IStoryControllerDocs {

    private final IStoryService service;
    private final StoryMapper mapper;

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    @Idempotent
    public ResponseHttp<StoryDTO> create(
            @RequestHeader("X-Idempotency-Key") String idempotencyKey,
            @Valid @ModelAttribute CreateStoryDTO dto,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        UserModel user = principal.getUser();

        StoryModel model = this.service.create(user.getId(), dto);

        return ResponseHttp.success(this.mapper.toDTO(model), "Story created!", idempotencyKey);
    }

    @Override
    @Idempotent
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    ) {
        StoryModel model = this.service.findById(id);
        boolean delete = this.service.delete(model);

        if (!delete)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ResponseHttp.error("Error the delete story", idempotencyKey));

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseHttp.success(null, "Story deleted", idempotencyKey));
    }

}
