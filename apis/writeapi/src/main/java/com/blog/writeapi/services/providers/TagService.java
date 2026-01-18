package com.blog.writeapi.services.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.dtos.tag.CreateTagDTO;
import com.blog.writeapi.dtos.tag.UpdateTagDTO;
import com.blog.writeapi.models.TagModel;
import com.blog.writeapi.repositories.TagRepository;
import com.blog.writeapi.services.interfaces.ITagService;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.annotations.valid.global.slugConstraint.SlugConstraint;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.mappers.TagMapper;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagService implements ITagService {

    private final TagRepository repository;
    private final Snowflake generator;
    private final TagMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public TagModel getByIdSimple(@IsId Long id) {
        return this.repository.findById(id).orElseThrow(
                () -> new ModelNotFoundException("Tag not found")
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TagModel> getById(@IsId Long id) { return this.repository.findById(id); }

    @Override
    @Transactional(readOnly = true)
    public Boolean existsById(@IsId Long id) { return this.repository.existsById(id); }

    @Override
    @Transactional(readOnly = true)
    public Optional<TagModel> getByName(String name) { return this.repository.findByName(name); }

    @Override
    @Transactional(readOnly = true)
    public Boolean existsByName(String name) { return this.repository.existsByName(name); }

    @Override
    @Transactional(readOnly = true)
    public Optional<TagModel> getBySlug(@SlugConstraint String slug) { return this.repository.findBySlug(slug); }

    @Override
    @Transactional(readOnly = true)
    public Boolean existsBySlug(@SlugConstraint String slug) { return this.repository.existsBySlug(slug); }

    @Override
    @Transactional
    public void delete(@IsModelInitialized TagModel tag) { this.repository.delete(tag); }

    @Override
    @Transactional
    public TagModel create(CreateTagDTO dto) {
        TagModel model = this.mapper.toModel(dto);
        model.setId(generator.nextId());

        TagModel save = this.repository.save(model);
        log.info("Tag saved is: {}", save);
        return save;
    }

    @Override
    @Transactional
    @Retry(name = "update-retry")
    public TagModel update(UpdateTagDTO dto, @IsModelInitialized TagModel tag) {
        mapper.merge(dto, tag);

        return this.repository.save(tag);
    }

}
