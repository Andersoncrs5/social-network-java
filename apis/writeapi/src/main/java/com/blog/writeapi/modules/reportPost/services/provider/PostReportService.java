package com.blog.writeapi.modules.reportPost.services.provider;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.reportPost.dto.CreatePostReportDTO;
import com.blog.writeapi.modules.reportPost.dto.UpdatePostReportDTO;
import com.blog.writeapi.modules.reportPost.model.PostReportModel;
import com.blog.writeapi.modules.reportPost.repository.PostReportRepository;
import com.blog.writeapi.modules.reportPost.services.interfaces.IPostReportService;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.enums.report.ModerationActionType;
import com.blog.writeapi.utils.enums.report.ReportStatus;
import com.blog.writeapi.utils.exceptions.BusinessRuleException;
import com.blog.writeapi.utils.exceptions.InternalServerErrorException;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.exceptions.UniqueConstraintViolationException;
import com.blog.writeapi.utils.mappers.PostReportMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class PostReportService implements IPostReportService {

    private final PostReportRepository repository;
    private final Snowflake snowflake;
    private final PostReportMapper mapper;
    private final ObjectMapper objectMapper;

    @Override
    public PostReportModel findByIdSimple(@IsId Long id) {
        return this.repository.findById(id).orElseThrow(() ->
            new ModelNotFoundException("Post Report not found")
        );
    }

    @Override
    public Boolean existsByPostAndUser(
            @IsModelInitialized PostModel post,
            @IsModelInitialized UserModel user
    ) {
        return this.repository.existsByPostAndUser(post, user);
    }

    @Override
    public void delete(
            @IsModelInitialized PostReportModel report,
            @IsId Long userId
    ) {
        OffsetDateTime limit = report.getCreatedAt().plusHours(24);
        if (OffsetDateTime.now().isAfter(limit)) {
            throw new BusinessRuleException("Reports can only be deleted within 24 hours of creation.", HttpStatus.FORBIDDEN);
        }

        if (!Objects.equals(report.getUser().getId(), userId)) {
            throw new BusinessRuleException("You have not permission to delete this report", HttpStatus.FORBIDDEN);
        }

        this.repository.delete(report);
    }

    @Override
    public PostReportModel create(
            CreatePostReportDTO dto,
            @IsModelInitialized PostModel post,
            @IsModelInitialized UserModel user
    ) {
        PostReportModel report = this.mapper.toModel(dto);

        report.setPost(post);
        report.setUser(user);
        report.setId(snowflake.nextId());
        report.setPostAuthorId(post.getAuthor().getId());
        report.setModerationActionType(ModerationActionType.NONE);
        report.setStatus(ReportStatus.PENDING);

        try {
            report.setPostContentSnapshot(objectMapper.writeValueAsString(post));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        try {
            return repository.save(report);
        } catch (DataIntegrityViolationException e) {
            String message = Optional.of(e.getMostSpecificCause())
                    .map(Throwable::getMessage)
                    .orElse("");

            if (message.toLowerCase().contains("uk_post_report"))
                throw new UniqueConstraintViolationException("You have already reported on this post.");

            throw new InternalServerErrorException("Error the create report to post");
        }
    }

    @Override
    public PostReportModel update(
            UpdatePostReportDTO dto,
            @IsModelInitialized PostReportModel report
    ) {
        this.mapper.merge(dto, report);

        try {
            return repository.save(report);
        } catch (DataIntegrityViolationException e) {
            String message = Optional.of(e.getMostSpecificCause())
                    .map(Throwable::getMessage)
                    .orElse("");

            if (message.toLowerCase().contains("uk_post_report"))
                throw new UniqueConstraintViolationException("You have already reported on this post.");

            throw new InternalServerErrorException("Error the create report to post");
        }
    }

    @Override
    public PostReportModel update(
            UpdatePostReportDTO dto,
            @IsModelInitialized PostReportModel report,
            @IsModelInitialized UserModel moderator
    ) {
        if (report.getModeratedAt() != null) {
            OffsetDateTime limit = report.getModeratedAt().plusDays(1);

            if (OffsetDateTime.now().isAfter(limit)) {
                throw new BusinessRuleException("The edit window for this report (24h) has expired.");
            }
        }

        this.mapper.merge(dto, report);
        report.setModerator(moderator);

        if (report.getModeratedAt() == null)
            report.setModeratedAt(OffsetDateTime.now());

        return repository.save(report);
    }

}
