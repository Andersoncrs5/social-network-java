package com.blog.writeapi.modules.postAttachment.controller.providers;

import com.blog.writeapi.configs.api.idempotent.Idempotent;
import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.post.services.interfaces.IPostService;
import com.blog.writeapi.modules.postAttachment.controller.docs.PostAttachmentControllerDocs;
import com.blog.writeapi.modules.postAttachment.dtos.CreatePostAttachmentDTO;
import com.blog.writeapi.modules.postAttachment.dtos.UpdatePostAttachmentDTO;
import com.blog.writeapi.modules.postAttachment.models.PostAttachmentModel;
import com.blog.writeapi.modules.postAttachment.service.docs.IPostAttachmentService;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.exceptions.ResourceOwnerMismatchException;
import com.blog.writeapi.utils.mappers.PostAttachmentMapper;
import com.blog.writeapi.utils.res.ResponseHttp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/post-attachment")
public class PostAttachmentController implements PostAttachmentControllerDocs {

    private final IPostAttachmentService service;
    private final IPostService postService;
    private final PostAttachmentMapper mapper;

    @Override
    @Idempotent
    public ResponseEntity<?> create(
            @Valid @ModelAttribute CreatePostAttachmentDTO dto,
            HttpServletRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    ) {
        UserModel user = principal.getUser();
        PostModel post = this.postService.getByIdSimple(dto.getPostId());

        Optional<PostAttachmentModel> optional = this.service.create(dto, user, post);

        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseHttp<>(
                    null,
                    "Error the sent the file",
                    idempotencyKey,
                    1,
                    false,
                    OffsetDateTime.now()
            ));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseHttp<>(
                mapper.toDTO(optional.get()),
                "File sent!",
                idempotencyKey,
                1,
                true,
                OffsetDateTime.now()
        ));
    }

    @Override
    public ResponseEntity<?> delete(
            @PathVariable @IsId Long id,
            HttpServletRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    ) {
        Long userId = principal.getId();
        PostAttachmentModel attachment = this.service.getByIdSimple(id);

        Boolean deleted = this.service.delete(attachment, userId);

        if (!deleted) {
            return new ResponseEntity<>(new ResponseHttp<>(
                    null,
                    "Error the sent the attachment",
                    idempotencyKey,
                    1,
                    false,
                    OffsetDateTime.now()
            ),HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new ResponseHttp<>(
                null,
                "Attachment deleted",
                idempotencyKey,
                1,
                true,
                OffsetDateTime.now()
        ), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> update(
            @PathVariable @IsId Long id,
            @RequestBody UpdatePostAttachmentDTO dto,
            HttpServletRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    ) {
        Long userId = principal.getId();
        PostAttachmentModel attachment = this.service.getByIdSimple(id);

        if (!Objects.equals(attachment.getUploader().getId(), userId)) {
            throw new ResourceOwnerMismatchException("You don't have permission to delete this attachment.");
        }

        PostAttachmentModel updated = this.service.updateMetadata(attachment, dto);

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHttp<>(
                mapper.toDTO(updated),
                "Attachment updated!",
                idempotencyKey,
                1,
                true,
                OffsetDateTime.now()
        ));
    }

}
