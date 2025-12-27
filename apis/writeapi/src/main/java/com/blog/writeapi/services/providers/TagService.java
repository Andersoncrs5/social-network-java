package com.blog.writeapi.services.providers;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.dtos.tag.CreateTagDTO;
import com.blog.writeapi.dtos.tag.UpdateTagDTO;
import com.blog.writeapi.models.TagModel;
import com.blog.writeapi.repositories.TagRepository;
import com.blog.writeapi.services.interfaces.ITagService;
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
    public TagModel getByIdSimple(Long id) {
        return this.repository.findById(id).orElseThrow(
                () -> new ModelNotFoundException("Tag not found")
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TagModel> getById(Long id) { return this.repository.findById(id); }

    @Override
    @Transactional(readOnly = true)
    public Boolean existsById(Long id) { return this.repository.existsById(id); }

    @Override
    @Transactional(readOnly = true)
    public Optional<TagModel> getByName(String name) { return this.repository.findByName(name); }

    @Override
    @Transactional(readOnly = true)
    public Boolean existsByName(String name) { return this.repository.existsByName(name); }

    @Override
    @Transactional(readOnly = true)
    public Optional<TagModel> getBySlug(String slug) { return this.repository.findBySlug(slug); }

    @Override
    @Transactional(readOnly = true)
    public Boolean existsBySlug(String slug) { return this.repository.existsBySlug(slug); }

    @Override
    @Transactional
    public void delete(TagModel tag) { this.repository.delete(tag); }

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
    public TagModel update(UpdateTagDTO dto, TagModel tag) {
        mapper.merge(dto, tag);

        return this.repository.save(tag);
    }

}
