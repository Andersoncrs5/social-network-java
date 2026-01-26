package com.blog.writeapi.controllers.providers;

import com.blog.writeapi.controllers.docs.PostAttachmentControllerDocs;
import com.blog.writeapi.dtos.postAttachment.CreatePostAttachmentDTO;
import com.blog.writeapi.models.PostAttachmentModel;
import com.blog.writeapi.models.PostModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.services.interfaces.IPostAttachmentService;
import com.blog.writeapi.services.interfaces.IPostService;
import com.blog.writeapi.services.interfaces.ITokenService;
import com.blog.writeapi.services.interfaces.IUserService;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.exceptions.ResourceOwnerMismatchException;
import com.blog.writeapi.utils.mappers.PostAttachmentMapper;
import com.blog.writeapi.utils.res.ResponseHttp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    private final IUserService userService;
    private final PostAttachmentMapper mapper;
    private final ITokenService tokenService;

    @Override
    public ResponseEntity<?> create(
            @Valid @ModelAttribute CreatePostAttachmentDTO dto,
            HttpServletRequest request
    ) {
        Long id = this.tokenService.extractUserIdFromRequest(request);
        UserModel user = this.userService.GetByIdSimple(id);
        PostModel post = this.postService.getByIdSimple(dto.getPostId());

        Optional<PostAttachmentModel> optional = this.service.create(dto, user, post);

        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseHttp<>(
                    null,
                    "Error the sent the file",
                    UUID.randomUUID().toString(),
                    1,
                    false,
                    OffsetDateTime.now()
            ));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseHttp<>(
                mapper.toDTO(optional.get()),
                "File sent!",
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        ));
    }

    @Override
    public ResponseEntity<?> delete(
            @PathVariable @IsId Long id,
            HttpServletRequest request
    ) {
        Long userId = this.tokenService.extractUserIdFromRequest(request);
        PostAttachmentModel attachment = this.service.getByIdSimple(id);

        if (!Objects.equals(attachment.getUploader().getId(), userId)) {
            throw new ResourceOwnerMismatchException("You don't have permission to delete this attachment.");
        }

        Boolean deleted = this.service.delete(attachment);

        if (!deleted) {
            return new ResponseEntity<>(new ResponseHttp<>(
                    null,
                    "Error the sent the attachment",
                    UUID.randomUUID().toString(),
                    1,
                    false,
                    OffsetDateTime.now()
            ),HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new ResponseHttp<>(
                null,
                "Attachment deleted",
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        ), HttpStatus.OK);
    }

}
