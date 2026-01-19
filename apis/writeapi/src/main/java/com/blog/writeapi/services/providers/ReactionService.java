package com.blog.writeapi.services.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.dtos.reaction.CreateReactionDTO;
import com.blog.writeapi.dtos.reaction.UpdateReactionDTO;
import com.blog.writeapi.models.ReactionModel;
import com.blog.writeapi.repositories.ReactionRepository;
import com.blog.writeapi.services.interfaces.IReactionService;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.mappers.ReactionMapper;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class ReactionService implements IReactionService {

    private final ReactionRepository repository;
    private final Snowflake generator;
    private final ReactionMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Boolean existsById(@IsId Long id) {
        return this.repository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean existsByEmojiUnicode(@NotBlank String uni) {
        return this.repository.existsByEmojiUnicodeIgnoreCase(uni);
    }

    @Override
    @Transactional(readOnly = true)
    public ReactionModel getByIdSimple(@IsId Long id) {
        return this.repository.findById(id).orElseThrow(
                () -> new ModelNotFoundException("Reaction not found")
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean existsByName(@NotBlank String name) {
        return this.repository.existsByNameIgnoreCase(name);
    }

    @Override
    @Transactional
    @Retry(name = "delete-retry")
    public void delete(@IsModelInitialized ReactionModel reaction) {
        this.repository.delete(reaction);
    }

    @Override
    @Transactional
    @Retry(name = "create-retry")
    public ReactionModel create(CreateReactionDTO dto) {
        ReactionModel model = this.mapper.toModel(dto);

        model.setId(this.generator.nextId());

        return this.repository.save(model);
    }

    @Override
    @Transactional
    @Retry(name = "update-retry")
    public ReactionModel update(UpdateReactionDTO dto, @IsModelInitialized ReactionModel reaction) {
        this.mapper.merge(dto, reaction);

        return this.repository.save(reaction);
    }

}
