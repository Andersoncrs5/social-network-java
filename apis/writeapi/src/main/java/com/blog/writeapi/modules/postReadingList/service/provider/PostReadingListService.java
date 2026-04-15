package com.blog.writeapi.modules.postReadingList.service.provider;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.postReadingList.gateway.PostReadingListModuleGateway;
import com.blog.writeapi.modules.postReadingList.model.PostReadingListModel;
import com.blog.writeapi.modules.postReadingList.repository.PostReadingListRepository;
import com.blog.writeapi.modules.postReadingList.service.interfaces.IPostReadingListService;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.classes.ResultToggle;
import com.blog.writeapi.utils.exceptions.BusinessRuleException;
import com.blog.writeapi.utils.exceptions.InternalServerErrorException;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.exceptions.UniqueConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class PostReadingListService implements IPostReadingListService {

    private final PostReadingListRepository repository;
    private final PostReadingListModuleGateway gateway;
    private final Snowflake generator;

    public void delete(@IsModelInitialized PostReadingListModel model) {
        repository.delete(model);
    }

    public PostReadingListModel findByUserIdAndPostIdSimple(@IsId Long userId, @IsId Long postId) {
        return repository.findByUserIdAndPostId(userId, postId)
                .orElseThrow(() -> new ModelNotFoundException("Post not found"));
    }

    public Optional<PostReadingListModel> findByUserIdAndPostId(@IsId Long userId, @IsId Long postId) {
        return repository.findByUserIdAndPostId(userId, postId);
    }

    public PostReadingListModel create(@IsId Long userId, @IsId Long postId) {
        PostModel post = this.gateway.findPostById(postId);
        UserModel user = this.gateway.findUserById(userId);

        PostReadingListModel read = new PostReadingListModel().toBuilder()
                .id(generator.nextId())
                .user(user)
                .post(post)
                .build();

        try {
            return repository.save(read);
        } catch (DataIntegrityViolationException e) {
            String message = e.getMostSpecificCause().getMessage();

            if (message != null && message.contains("uk_post_user_reading_list")) {
                throw new UniqueConstraintViolationException(
                        "This post already has this type assigned."
                );
            }

            throw new BusinessRuleException("Database integrity error: " + message);
        } catch (Exception e) {
            throw new InternalServerErrorException("Error creating report association.");
        }
    }

    @Transactional
    public ResultToggle<PostReadingListModel> toggle(@IsId Long userId, @IsId Long postId) {
        Optional<PostReadingListModel> optional = this.repository.findByUserIdAndPostId(userId, postId);

        if (optional.isPresent()) {
            this.delete(optional.get());

            return ResultToggle.removed();
        }

        PostReadingListModel model = this.create(userId, postId);

        return ResultToggle.added(model);
    }

}
