package com.blog.writeapi.modules.pinnedPost.service.provider;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.pinnedPost.dto.CreatePinnedPostDTO;
import com.blog.writeapi.modules.pinnedPost.dto.UpdatePinnedPostDTO;
import com.blog.writeapi.modules.pinnedPost.gateway.PinnedPostServiceModuleGateway;
import com.blog.writeapi.modules.pinnedPost.model.PinnedPostModel;
import com.blog.writeapi.modules.pinnedPost.repository.PinnedPostRepository;
import com.blog.writeapi.modules.pinnedPost.service.interfaces.IPinnedPostService;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.exceptions.BusinessRuleException;
import com.blog.writeapi.utils.exceptions.InternalServerErrorException;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.exceptions.UniqueConstraintViolationException;
import com.blog.writeapi.utils.mappers.PinnedPostMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PinnedPostService implements IPinnedPostService {

    private static final int MAX_PINNED_POSTS = 10;
    private final PinnedPostRepository repository;
    private final PinnedPostMapper mapper;
    private final Snowflake generator;
    private final PinnedPostServiceModuleGateway gateway;

    @Override
    public boolean existsByUserIdAndPostId(
            @IsId Long userId,
            @IsId Long postId
    ) {
        return repository.existsByUserIdAndPostId(userId, postId);
    }

    @Override
    public PinnedPostModel findByIdSimple(@IsId Long id) {
        return repository.findById(id).orElseThrow(() -> new ModelNotFoundException("PinnedPost not found"));
    }

    @Override
    public boolean existsByUserIdAndOrderIndex(
            @IsId Long userId,
            int index
    ) {
        return repository.existsByUserIdAndOrderIndex(userId, index);
    }

    @Override
    public void delete(@IsModelInitialized PinnedPostModel pinned) {
        this.repository.delete(pinned);
    }

    @Override
    public PinnedPostModel create(
            @IsId Long userId,
            CreatePinnedPostDTO dto
    ) {
        int currentCount = repository.countByUserId(userId);

        if (currentCount >= MAX_PINNED_POSTS) {
            throw new BusinessRuleException(
                    String.format("You have reached the maximum limit of %d pinned posts.", MAX_PINNED_POSTS)
            );
        }

        UserModel user = this.gateway.findUserById(userId);
        PostModel post = this.gateway.findPostById(dto.postId());

        PinnedPostModel pinnedPost = new PinnedPostModel().toBuilder()
                .id(generator.nextId())
                .post(post)
                .user(user)
                .orderIndex(dto.orderIndex())
                .build();

        try {
            return repository.save(pinnedPost);
        } catch (DataIntegrityViolationException e) {
            String message = e.getMostSpecificCause().getMessage();

            if (message != null && message.contains("uk_pinned_posts_user_id_and_post_id")) {
                throw new UniqueConstraintViolationException(
                        "This post is already pinned to your profile."
                );
            }

            if (message != null && message.contains("uk_pinned_posts_user_id_and_order_index")) {
                throw new UniqueConstraintViolationException(
                        "There is already a post pinned at position " + dto.orderIndex() + "."
                );
            }

            throw new BusinessRuleException("Database integrity error: " + message);
        } catch (Exception e) {
            log.error("Error creating PostReportType", e);
            throw new InternalServerErrorException("Error creating report association.");
        }
    }

    @Override
    public PinnedPostModel update(
            @IsModelInitialized PinnedPostModel pinned,
            UpdatePinnedPostDTO dto
    ) {
        mapper.updateModel(dto, pinned);

        return repository.save(pinned);
    }

}
