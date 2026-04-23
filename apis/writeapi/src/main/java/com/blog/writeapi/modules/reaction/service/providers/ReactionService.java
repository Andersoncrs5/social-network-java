package com.blog.writeapi.modules.reaction.service.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.reaction.dtos.CreateReactionDTO;
import com.blog.writeapi.modules.reaction.dtos.UpdateReactionDTO;
import com.blog.writeapi.modules.reaction.models.ReactionModel;
import com.blog.writeapi.modules.reaction.repository.ReactionRepository;
import com.blog.writeapi.modules.reaction.service.docs.IReactionService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.exceptions.InternalServerErrorException;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.exceptions.UniqueConstraintViolationException;
import com.blog.writeapi.utils.mappers.ReactionMapper;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

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
    @Cacheable(value = "reaction", key = "#id")
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
    @Retry(name = "delete-retry")
    @CacheEvict(value = "reaction", key = "#reaction.id")
    public void delete(@IsModelInitialized ReactionModel reaction) {
        this.repository.delete(reaction);
    }

    @Override
    @Retry(name = "create-retry")
    @CachePut(value = "reaction", key = "#result.id")
    public ReactionModel create(CreateReactionDTO dto) {
        ReactionModel model = this.mapper.toModel(dto);

        model.setId(this.generator.nextId());

        try {
            return this.repository.save(model);
        } catch (DataIntegrityViolationException e) {
            String message = Optional.of(e.getMostSpecificCause())
                    .map(Throwable::getMessage)
                    .orElse("")
                    .toLowerCase();

            if (message.toLowerCase().contains("idx_reactions_name")) {
                throw new UniqueConstraintViolationException("Reaction name already exists: " + dto.name());
            }

            if (message.toLowerCase().contains("idx_reactions_emoji_url")) {
                throw new UniqueConstraintViolationException("Reaction emoji URL already exists: " + dto.emojiUrl());
            }

            throw new InternalServerErrorException("Error creating reaction: " + e.getMessage());
        } catch (Exception e) {
            throw new InternalServerErrorException("An unexpected error occurred while saving the reaction.");
        }
    }

    @Override
    @Transactional
    @Retry(name = "update-retry")
    @CachePut(value = "reaction", key = "#result.id")
    public ReactionModel update(UpdateReactionDTO dto, @IsModelInitialized ReactionModel reaction) {
        this.mapper.merge(dto, reaction);

        return this.repository.save(reaction);
    }

}
