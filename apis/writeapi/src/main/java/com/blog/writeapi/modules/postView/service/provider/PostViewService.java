package com.blog.writeapi.modules.postView.service.provider;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.configs.api.metadata.ClientMetadataDTO;
import com.blog.writeapi.modules.metric.dto.PostMetricEventDTO;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.postView.gateway.PostViewModuleGateway;
import com.blog.writeapi.modules.postView.model.PostViewModel;
import com.blog.writeapi.modules.postView.repository.PostViewRepository;
import com.blog.writeapi.modules.postView.service.interfaces.IPostViewService;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.enums.metric.ActionEnum;
import com.blog.writeapi.utils.enums.metric.PostMetricEnum;
import com.blog.writeapi.utils.exceptions.BusinessRuleException;
import com.blog.writeapi.utils.exceptions.InternalServerErrorException;
import com.blog.writeapi.utils.exceptions.UniqueConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostViewService implements IPostViewService {

    private final PostViewRepository repository;
    private final Snowflake generator;
    private final PostViewModuleGateway gateway;

    @Override
    public boolean existsByUserAndPost(
            @IsModelInitialized UserModel user,
            @IsModelInitialized PostModel post
    ) {
        return this.repository.existsByUserAndPost(user, post);
    }

    public PostViewModel create(
            UserModel user,
            PostModel post,
            ClientMetadataDTO metadata,
            LocalDate today
    ) {
        if (!user.getId().equals(post.getAuthor().getId()) && this.gateway.isBlocked(user.getId(), post.getAuthor().getId())) {
            throw new BusinessRuleException("You cannot react to a story from a blocked user.");
        }

        PostViewModel view = PostViewModel.builder()
                .id(generator.nextId())
                .post(post)
                .user(user)
                .ipAddress(metadata.ipAddress())
                .userAgent(metadata.userAgent())
                .fingerprint(metadata.fingerprint())
                .viewedAtDate(today)
                .bot(metadata.isBot())
                .build();

        try {
            PostViewModel save = this.repository.save(view);
            this.gateway.handleMetric(
                    PostMetricEventDTO.create(save.getPost().getId(), PostMetricEnum.VIEW, ActionEnum.SUM)
            );
            return save;
        } catch (DataIntegrityViolationException e) {
            String message = Optional.of(e.getMostSpecificCause())
                    .map(Throwable::getMessage)
                    .orElse("").toLowerCase();

            if (message.contains("uk_post_view_daily")) {
                throw new UniqueConstraintViolationException("User already view this post");
            }
            throw new BusinessRuleException("Database integrity error: " + message);
        } catch (Exception e) {
            log.error("Error creating post view: ", e);
            throw new InternalServerErrorException("Error creating report association.");
        }
    }

    @Override
    public void delete(@IsModelInitialized PostViewModel view) {
        repository.delete(view);
        this.gateway.handleMetric(
                PostMetricEventDTO.create(view.getPost().getId(), PostMetricEnum.VIEW, ActionEnum.RED)
        );
    }

    @Override
    public void recordView(
            @IsModelInitialized PostModel post,
            @IsModelInitialized UserModel user,
            ClientMetadataDTO metadata
    ) {
        LocalDate today = LocalDate.now();

        if (post.getAuthor().getId().equals(user.getId())) { return; }

        boolean exists = repository.existsByUserAndPostAndViewedAtDate(user, post, today);

        if (!exists) {
            try {
                this.create(user, post, metadata, today);
            } catch (UniqueConstraintViolationException e) {
                log.debug("Double click or race condition ignored for user {} on post {}", user.getId(), post.getId());
            }
        }

    }

    @Override
    public boolean existsByUserAndPostAndViewDate(
            @IsModelInitialized UserModel user,
            @IsModelInitialized PostModel post,
            LocalDate viewedAtDate
    ) {
        return repository.existsByUserAndPostAndViewedAtDate(user, post, viewedAtDate);
    }
}
