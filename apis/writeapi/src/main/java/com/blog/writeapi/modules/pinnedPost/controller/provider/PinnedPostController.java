package com.blog.writeapi.modules.pinnedPost.controller.provider;

import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.pinnedPost.controller.docs.IPinnedPostControllerDocs;
import com.blog.writeapi.modules.pinnedPost.dto.CreatePinnedPostDTO;
import com.blog.writeapi.modules.pinnedPost.dto.UpdatePinnedPostDTO;
import com.blog.writeapi.modules.pinnedPost.model.PinnedPostModel;
import com.blog.writeapi.modules.pinnedPost.service.interfaces.IPinnedPostService;
import com.blog.writeapi.utils.exceptions.ResourceOwnerMismatchException;
import com.blog.writeapi.utils.mappers.PinnedPostMapper;
import com.blog.writeapi.utils.res.ResponseHttp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/pinned-post")
public class PinnedPostController implements IPinnedPostControllerDocs {

    private final IPinnedPostService service;
    private final PinnedPostMapper mapper;

    @Override
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        PinnedPostModel pinned = service.findByIdSimple(id);

        if (!Objects.equals(pinned.getUser().getId(), principal.getId())) {
            throw new ResourceOwnerMismatchException("This post is not your!");
        }

        this.service.delete(pinned);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(new ResponseHttp<>(
                null,
                "Pinned removed",
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
            )
        );
    }

    @Override
    public ResponseEntity<?> create(
            @RequestBody CreatePinnedPostDTO dto,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        PinnedPostModel model = this.service.create(principal.getId(), dto);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(new ResponseHttp<>(
                    mapper.toDTO(model),
                    "post pinned!",
                    UUID.randomUUID().toString(),
                    1,
                    true,
                    OffsetDateTime.now()
                )
            );
    }

    @Override
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody UpdatePinnedPostDTO dto,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        PinnedPostModel pinned = this.service.findByIdSimple(id);

        if (!Objects.equals(pinned.getUser().getId(), principal.getId())) {
            throw new ResourceOwnerMismatchException("This post is not your!");
        }

        PinnedPostModel updated = this.service.update(pinned, dto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseHttp<>(
                                mapper.toDTO(updated),
                                "post pinned updated!",
                                UUID.randomUUID().toString(),
                                1,
                                true,
                                OffsetDateTime.now()
                        )
                );
    }

}
