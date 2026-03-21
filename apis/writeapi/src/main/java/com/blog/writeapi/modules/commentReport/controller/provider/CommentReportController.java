package com.blog.writeapi.modules.commentReport.controller.provider;

import com.blog.writeapi.modules.comment.models.CommentModel;
import com.blog.writeapi.modules.comment.service.docs.ICommentService;
import com.blog.writeapi.modules.commentReport.controller.doc.ICommentReportControllerDocs;
import com.blog.writeapi.modules.commentReport.dto.CreateCommentReportDTO;
import com.blog.writeapi.modules.commentReport.dto.UpdateCommentReportDTO;
import com.blog.writeapi.modules.commentReport.model.CommentReportModel;
import com.blog.writeapi.modules.commentReport.service.interfaces.ICommentReportService;
import com.blog.writeapi.modules.reportPost.model.PostReportModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.user.service.docs.IUserService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.exceptions.BusinessRuleException;
import com.blog.writeapi.utils.mappers.CommentReportMapper;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.services.interfaces.ITokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/v1/comment-report")
public class CommentReportController implements ICommentReportControllerDocs {

    private final ICommentReportService service;
    private final ICommentService commentService;
    private final IUserService userService;
    private final ITokenService tokenService;
    private final CommentReportMapper mapper;

    @Override
    public ResponseEntity<?> create(
            @RequestBody @Valid CreateCommentReportDTO dto,
            HttpServletRequest request
    ) {
        Long userId = this.tokenService.extractUserIdFromRequest(request);

        UserModel user = this.userService.GetByIdSimple(userId);
        CommentModel comment = this.commentService.getByIdSimple(dto.commentId());

        boolean exists = this.service.existsByCommentAndUser(comment, user);

        if (exists) {
            throw new BusinessRuleException("You already report this comment!", HttpStatus.CONFLICT);
        }

        CommentReportModel model = this.service.create(dto, comment, user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                    new ResponseHttp<>(
                        this.mapper.toDTO(model),
                        "Report created with successfully",
                        UUID.randomUUID().toString(),
                        1,
                        true,
                        OffsetDateTime.now()
                    )
                );
    }

    @Override
    public ResponseEntity<?> delete(
            @PathVariable @IsId Long id,
            HttpServletRequest request
    ) {
        Long userId = this.tokenService.extractUserIdFromRequest(request);

        CommentReportModel report = this.service.findByIdSimple(id);

        if (!Objects.equals(report.getUser().getId(), userId)) {
            throw new BusinessRuleException("You have not permission to delete this report", HttpStatus.FORBIDDEN);
        }

        this.service.delete(report);

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHttp<>(
                null,
                "Report deleted with successfully",
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        ));
    }

    @Override
    public ResponseEntity<?> update(
            @PathVariable @IsId Long id,
            @Valid @RequestBody UpdateCommentReportDTO dto,
            HttpServletRequest request
    ) {
        Long moderatorId = this.tokenService.extractUserIdFromRequest(request);
        UserModel moderator = this.userService.GetByIdSimple(moderatorId);

        CommentReportModel report = this.service.findByIdSimple(id);

        CommentReportModel reportUpdated = this.service.update(dto, report, moderator);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                    new ResponseHttp<>(
                        this.mapper.toDTO(reportUpdated),
                        "Report updated with successfully",
                        UUID.randomUUID().toString(),
                        1,
                        true,
                        OffsetDateTime.now()
                    )
                );
    }

}
