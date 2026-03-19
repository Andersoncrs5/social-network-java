package com.blog.writeapi.modules.commentReport.service.provider;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.comment.models.CommentModel;
import com.blog.writeapi.modules.commentReport.dto.CreateCommentReportDTO;
import com.blog.writeapi.modules.commentReport.dto.UpdateCommentReportDTO;
import com.blog.writeapi.modules.commentReport.model.CommentReportModel;
import com.blog.writeapi.modules.commentReport.repository.CommentReportRepository;
import com.blog.writeapi.modules.commentReport.service.interfaces.ICommentReportService;
import com.blog.writeapi.modules.reportPost.dto.CreatePostReportDTO;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.enums.report.ModerationActionType;
import com.blog.writeapi.utils.enums.report.ReportStatus;
import com.blog.writeapi.utils.exceptions.BusinessRuleException;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.mappers.CommentReportMapper;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.OffsetDateTime;
import java.util.Optional;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class CommentReportService implements ICommentReportService {

    private final CommentReportRepository repository;
    private final CommentReportMapper mapper;
    private final Snowflake snowflake;
    private final ObjectMapper objectMapper;

    @Override
    public CommentReportModel findByIdSimple(@IsId Long id) {
        return this.repository.findById(id).orElseThrow(() ->
                new ModelNotFoundException("Comment Report not found")
        );
    }

    @Override
    public Optional<CommentReportModel> findByCommentAndUser(
            @IsModelInitialized CommentModel comment,
            @IsModelInitialized UserModel user
    ) {
        return this.repository.findByCommentAndUser(comment, user);
    }

    @Override
    public void delete(
            @IsModelInitialized CommentReportModel model
    ) {
        if (model.getModeratedAt() != null) {
            OffsetDateTime limit = model.getModeratedAt().plusDays(1);

            if (OffsetDateTime.now().isAfter(limit)) {
                throw new BusinessRuleException("The edit window for this report (24h) has expired.", HttpStatus.FORBIDDEN);
            }
        }

        this.repository.delete(model);
    }

    @Override
    public Boolean existsByCommentAndUser(
            @IsModelInitialized CommentModel comment,
            @IsModelInitialized UserModel user
    ) {
        return this.repository.existsByCommentAndUser(comment, user);
    }

    @Override
    public CommentReportModel create(
            CreateCommentReportDTO dto,
            @IsModelInitialized CommentModel comment,
            @IsModelInitialized UserModel user
    ) {
        CommentReportModel report = this.mapper.toModel(dto);

        report.setComment(comment);
        report.setUser(user);
        report.setId(snowflake.nextId());
        report.setCommentAuthorId(comment.getAuthor().getId());
        report.setModerationActionType(ModerationActionType.NONE);
        report.setStatus(ReportStatus.PENDING);

        try {
            report.setCommentContentSnapshot(objectMapper.writeValueAsString(comment));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return repository.save(report);
    }

    @Override
    public CommentReportModel update(
            UpdateCommentReportDTO dto,
            @IsModelInitialized CommentReportModel model,
            @IsModelInitialized UserModel moderator
    ) {
        if (model.getModeratedAt() != null) {
            OffsetDateTime limit = model.getModeratedAt().plusDays(1);

            if (OffsetDateTime.now().isAfter(limit)) {
                throw new BusinessRuleException("The edit window for this report (24h) has expired.", HttpStatus.FORBIDDEN);
            }
        }

        this.mapper.merge(dto, model);
        model.setModerator(moderator);

        if (model.getModeratedAt() == null)
            model.setModeratedAt(OffsetDateTime.now());

        return repository.save(model);
    }



}
