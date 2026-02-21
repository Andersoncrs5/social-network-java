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
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.mappers.PostReportMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.OffsetDateTime;

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
    public void delete(@IsModelInitialized PostReportModel report) {
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

        return repository.save(report);
    }

    @Override
    public PostReportModel update(
            UpdatePostReportDTO dto,
            @IsModelInitialized PostReportModel report
    ) {
        this.mapper.merge(dto, report);

        return repository.save(report);
    }

    @Override
    public PostReportModel update(
            UpdatePostReportDTO dto,
            @IsModelInitialized PostReportModel report,
            @IsModelInitialized UserModel moderator
    ) {
        this.mapper.merge(dto, report);
        report.setModerator(moderator);

        if (report.getModeratedAt() == null)
            report.setModeratedAt(OffsetDateTime.now());

        return repository.save(report);
    }

}
