package com.blog.writeapi.services.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.models.CommentModel;
import com.blog.writeapi.models.CommentReactionModel;
import com.blog.writeapi.models.ReactionModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.repositories.CommentReactionRepository;
import com.blog.writeapi.services.interfaces.ICommentReactionService;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class CommentReactionService implements ICommentReactionService {

    private final CommentReactionRepository repository;
    private final Snowflake generator;

    @Override
    @Transactional(readOnly = true)
    public Optional<CommentReactionModel> findByUserAndComment(UserModel user, CommentModel comment) {
        return this.repository.findByUserAndComment(user, comment);
    }

    @Override
    @Retry(name = "create-retry")
    public CommentReactionModel create(CommentModel comment, ReactionModel reaction, UserModel user) {
        CommentReactionModel model = new CommentReactionModel().toBuilder()
                .comment(comment)
                .reaction(reaction)
                .user(user)
                .id(this.generator.nextId())
                .build();

        return this.repository.save(model);
    }

    @Override
    @Transactional
    @Retry(name = "delete-retry")
    public void delete(CommentReactionModel model) {
        this.repository.delete(model);
    }

    @Override
    @Transactional
    @Retry(name = "update-retry")
    public CommentReactionModel updateSimple(CommentReactionModel model) {
        return repository.save(model);
    }

}
