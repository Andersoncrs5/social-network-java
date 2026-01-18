package com.blog.writeapi.services.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.dtos.commentVote.ToggleCommentVoteDTO;
import com.blog.writeapi.models.CommentModel;
import com.blog.writeapi.models.CommentVoteModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.repositories.CommentVoteRepository;
import com.blog.writeapi.services.interfaces.ICommentVoteService;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentVoteService implements ICommentVoteService {

    private final CommentVoteRepository repository;
    private final Snowflake generator;

    @Override
    @Transactional(readOnly = true)
    public Optional<CommentVoteModel> findByUserAndComment(
            @IsModelInitialized UserModel user,
            @IsModelInitialized CommentModel comment
    ) {
        return this.repository.findByUserAndComment(user, comment);
    }

    @Override
    @Transactional
    @Retry(name = "delete-retry")
    public void delete(
            @IsModelInitialized CommentVoteModel vote
    ) {
        repository.delete(vote);
    }

    @Override
    @Transactional
    @Retry(name = "create-retry")
    public CommentVoteModel create(
            ToggleCommentVoteDTO dto,
            @IsModelInitialized CommentModel comment,
            @IsModelInitialized UserModel user
    ) {
        CommentVoteModel vote = new CommentVoteModel().toBuilder()
                .id(this.generator.nextId())
                .type(dto.type())
                .user(user)
                .comment(comment)
                .build();

        return this.repository.save(vote);
    }

    @Override
    @Transactional
    @Retry(name = "update-retry")
    public CommentVoteModel updateSimple(@IsModelInitialized CommentVoteModel vote) {
        return this.repository.save(vote);
    }

}
