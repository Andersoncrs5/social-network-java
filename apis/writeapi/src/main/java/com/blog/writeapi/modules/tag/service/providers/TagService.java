package com.blog.writeapi.modules.tag.service.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.tag.dtos.CreateTagDTO;
import com.blog.writeapi.modules.tag.dtos.UpdateTagDTO;
import com.blog.writeapi.modules.tag.models.TagModel;
import com.blog.writeapi.modules.tag.repository.TagRepository;
import com.blog.writeapi.modules.tag.service.docs.ITagService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.annotations.validations.global.slugConstraint.SlugConstraint;
import com.blog.writeapi.utils.annotations.validations.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.mappers.TagMapper;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
    @Cacheable(value = "tag", key = "#id")
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
    public Optional<TagModel> getByName(String name) { return this.repository.findByNameIgnoreCase(name); }

    @Override
    @Transactional(readOnly = true)
    public Boolean existsByName(String name) { return this.repository.existsByNameIgnoreCase(name); }

    @Override
    @Transactional(readOnly = true)
    public Optional<TagModel> getBySlug(@SlugConstraint String slug) { return this.repository.findBySlugIgnoreCase(slug); }

    @Override
    @Transactional(readOnly = true)
    public Boolean existsBySlug(@SlugConstraint String slug) { return this.repository.existsBySlugIgnoreCase(slug); }

    @Override
    @Transactional
    @CacheEvict(value = "tag", key = "#tag.id")
    public void delete(@IsModelInitialized TagModel tag) { this.repository.delete(tag); }

    @Override
    @CachePut(value = "tag", key = "#result.id")
    public TagModel create(CreateTagDTO dto) {
        TagModel model = this.mapper.toModel(dto);
        model.setId(generator.nextId());

        TagModel save = this.repository.save(model);
        log.info("Tag saved is: {}", save);
        return save;
    }

    @Override
    @Retry(name = "update-retry")
    @CachePut(value = "tag", key = "#result.id")
    public TagModel update(UpdateTagDTO dto, @IsModelInitialized TagModel tag) {
        mapper.merge(dto, tag);
        return this.repository.save(tag);
    }
}
