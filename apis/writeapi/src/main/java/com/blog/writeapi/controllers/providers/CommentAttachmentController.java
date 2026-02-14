package com.blog.writeapi.controllers.providers;

import com.blog.writeapi.controllers.docs.ICommentAttachmentController;
import com.blog.writeapi.dtos.commentAttachment.CreateCommentAttachmentDTO;
import com.blog.writeapi.dtos.commentAttachment.UpdateCommentAttachmentDTO;
import com.blog.writeapi.models.CommentAttachmentModel;
import com.blog.writeapi.models.CommentModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.services.interfaces.ICommentAttachmentService;
import com.blog.writeapi.services.interfaces.ICommentService;
import com.blog.writeapi.services.interfaces.ITokenService;
import com.blog.writeapi.services.interfaces.IUserService;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.exceptions.ResourceOwnerMismatchException;
import com.blog.writeapi.utils.mappers.CommentAttachmentMapper;
import com.blog.writeapi.utils.res.ResponseHttp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("v1/comment-attachment")
@RequiredArgsConstructor
public class CommentAttachmentController implements ICommentAttachmentController {

    private final ICommentAttachmentService service;
    private final CommentAttachmentMapper mapper;
    private final ICommentService commentService;
    private final ITokenService tokenService;
    private final IUserService userService;

    @Override
    public ResponseEntity<?> create(
            @Valid @ModelAttribute CreateCommentAttachmentDTO dto,
            HttpServletRequest request
    ) {
        Long userID = this.tokenService.extractUserIdFromRequest(request);
        UserModel user = this.userService.GetByIdSimple(userID);
        CommentModel comment = this.commentService.getByIdSimple(dto.getCommentId());

        Optional<CommentAttachmentModel> optional = this.service.create(dto, user, comment);

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

        CommentAttachmentModel attachment = this.service.findByIdSimple(id);

        if (!Objects.equals(attachment.getUploader().getId(), userId))
            throw new ResourceOwnerMismatchException("You don't have permission to delete this attachment.");

        boolean deleted = this.service.delete(attachment);

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

    @Override
    public ResponseEntity<?> update(
            @PathVariable @IsId Long id,
            @RequestBody UpdateCommentAttachmentDTO dto,
            HttpServletRequest request
    ) {
        Long userId = this.tokenService.extractUserIdFromRequest(request);
        CommentAttachmentModel attachment = this.service.findByIdSimple(id);

        if (!Objects.equals(attachment.getUploader().getId(), userId))
            throw new ResourceOwnerMismatchException("You don't have permission to delete this attachment.");

        CommentAttachmentModel attachmentUpdated = this.service.update(attachment, dto);

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHttp<>(
                mapper.toDTO(attachmentUpdated),
                "Attachment updated!",
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        ));
    }


}
