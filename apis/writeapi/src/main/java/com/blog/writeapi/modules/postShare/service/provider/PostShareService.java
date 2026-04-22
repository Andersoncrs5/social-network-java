package com.blog.writeapi.modules.postShare.service.provider;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.postShare.gateway.PostShareModuleGateway;
import com.blog.writeapi.modules.postShare.model.PostShareModel;
import com.blog.writeapi.modules.postShare.repository.PostShareRepository;
import com.blog.writeapi.modules.postShare.service.interfaces.IPostShareService;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.enums.postShare.SharePlatformEnum;
import com.blog.writeapi.utils.exceptions.BusinessRuleException;
import com.blog.writeapi.utils.exceptions.InternalServerErrorException;
import com.blog.writeapi.utils.exceptions.UniqueConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class PostShareService implements IPostShareService {

    private final PostShareRepository repository;
    private final Snowflake generator;
    private final PostShareModuleGateway gateway;

    public Optional<PostShareModel> findByUserIdAndPostId(
            @IsId Long userId,
            @IsId Long postId
    ) {
        return this.repository.findByUserIdAndPostId(userId, postId);
    }

    public Optional<PostShareModel> findByUserIdAndPostIdAndPlatform(
            @IsId Long userId,
            @IsId Long postId,
            SharePlatformEnum platform
    ) {
        return this.repository.findByUserIdAndPostIdAndPlatform(userId, postId, platform);
    }

    public boolean existsByUserIdAndPostIdAndPlatform(
            @IsId Long userId,
            @IsId Long postId,
            SharePlatformEnum platform
    ) {
        return repository.existsByUserIdAndPostIdAndPlatform(
                userId,
                postId,
                platform
        );
    }

    public PostShareModel create(
            @IsId Long userId,
            @IsId Long postId,
            SharePlatformEnum platform
    ) {
        UserModel user = this.gateway.findUserById(userId);
        PostModel post = this.gateway.findPostById(postId);

        if (!user.getId().equals(post.getAuthor().getId())) {
            if (this.gateway.isBlocked(user.getId(), post.getAuthor().getId())) {
                throw new BusinessRuleException("You cannot share a post from a blocked user.");
            }
        }

        PostShareModel model = new PostShareModel().toBuilder()
                .id(generator.nextId())
                .user(user)
                .post(post)
                .platform(platform)
                .build();

        try {
            return this.repository.save(model);
        } catch (DataIntegrityViolationException e) {
            String message = e.getMostSpecificCause().getMessage();

            if (message != null && message.contains("uk_post_shares")) {
                throw new UniqueConstraintViolationException(
                        "This post already has this shared."
                );
            }

            throw new BusinessRuleException("Database integrity error: " + message);
        } catch (Exception e) {
            throw new InternalServerErrorException("Error creating share to post.");
        }

    }

    public void createIfNotExists(
            @IsId Long userId,
            @IsId Long postId,
            SharePlatformEnum platform
    ) {
        Optional<PostShareModel> optional = this.repository.findByUserIdAndPostIdAndPlatform(userId, userId, platform);

        optional.orElseGet(() -> this.create(userId, postId, platform));
    }

}
