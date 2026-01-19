package com.blog.writeapi.unit;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.dtos.tag.CreateTagDTO;
import com.blog.writeapi.dtos.tag.UpdateTagDTO;
import com.blog.writeapi.models.TagModel;
import com.blog.writeapi.repositories.TagRepository;
import com.blog.writeapi.services.providers.TagService;
import com.blog.writeapi.utils.mappers.TagMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TagServiceTest {

    @Mock private TagRepository repository;
    @Mock private Snowflake generator;
    @Mock private TagMapper mapper;

    @InjectMocks private TagService service;

    TagModel tag = new TagModel().toBuilder()
            .id(1998780200074176609L)
            .name("springBoot")
            .slug("spring-boot")
            .description("AnyDesc")
            .isActive(true)
            .isVisible(true)
            .isSystem(false)
            .postsCount(0L)
            .version(1L)
            .lastUsedAt(OffsetDateTime.now())
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    @Test
    void shouldGetTag() {
        when(repository.findById(this.tag.getId())).thenReturn(Optional.of(this.tag));

        Optional<TagModel> optional = this.service.getById(this.tag.getId());

        assertThat(optional.isEmpty()).isFalse();
        assertThat(optional.get()).isEqualTo(this.tag);

        verify(repository, times(1)).findById(this.tag.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnNullWhenGetTagById() {
        when(repository.findById(this.tag.getId())).thenReturn(Optional.empty());

        Optional<TagModel> optional = this.service.getById(this.tag.getId());

        assertThat(optional.isEmpty()).isTrue();

        verify(repository, times(1)).findById(this.tag.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnTrueWhenCheckExistsTagById() {
        when(repository.existsById(this.tag.getId())).thenReturn(true);

        Boolean exists = this.service.existsById(this.tag.getId());

        assertThat(exists).isTrue();

        verify(repository, times(1)).existsById(this.tag.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnFalseWhenCheckExistsTagById() {
        when(repository.existsById(this.tag.getId())).thenReturn(false);

        Boolean exists = this.service.existsById(this.tag.getId());

        assertThat(exists).isFalse();

        verify(repository, times(1)).existsById(this.tag.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldGetTagByName() {
        when(repository.findByNameIgnoreCase(this.tag.getName())).thenReturn(Optional.of(this.tag));

        Optional<TagModel> optional = this.service.getByName(this.tag.getName());

        assertThat(optional.isEmpty()).isFalse();
        assertThat(optional.get()).isEqualTo(this.tag);

        verify(repository, times(1)).findByNameIgnoreCase(this.tag.getName());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnNullWhenGetTagByName() {
        when(repository.findByNameIgnoreCase(this.tag.getName())).thenReturn(Optional.empty());

        Optional<TagModel> optional = this.service.getByName(this.tag.getName());

        assertThat(optional.isEmpty()).isTrue();

        verify(repository, times(1)).findByNameIgnoreCase(this.tag.getName());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnTrueWhenCheckExistsTagByName() {
        when(repository.existsByNameIgnoreCase(this.tag.getName())).thenReturn(true);

        Boolean exists = this.service.existsByName(this.tag.getName());

        assertThat(exists).isTrue();

        verify(repository, times(1)).existsByNameIgnoreCase(this.tag.getName());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnFalseWhenCheckExistsTagByName() {
        when(repository.existsByNameIgnoreCase(this.tag.getName())).thenReturn(false);

        Boolean exists = this.service.existsByName(this.tag.getName());

        assertThat(exists).isFalse();

        verify(repository, times(1)).existsByNameIgnoreCase(this.tag.getName());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldGetTagBySlug() {
        when(repository.findBySlugIgnoreCase(this.tag.getSlug())).thenReturn(Optional.of(this.tag));

        Optional<TagModel> optional = this.service.getBySlug(this.tag.getSlug());

        assertThat(optional.isEmpty()).isFalse();
        assertThat(optional.get()).isEqualTo(this.tag);

        verify(repository, times(1)).findBySlugIgnoreCase(this.tag.getSlug());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnNullWhenGetTagBySlug() {
        when(repository.findBySlugIgnoreCase(this.tag.getSlug())).thenReturn(Optional.empty());

        Optional<TagModel> optional = this.service.getBySlug(this.tag.getSlug());

        assertThat(optional.isEmpty()).isTrue();

        verify(repository, times(1)).findBySlugIgnoreCase(this.tag.getSlug());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnTrueWhenCheckExistsTagBySlug() {
        when(repository.existsBySlugIgnoreCase(this.tag.getSlug())).thenReturn(true);

        Boolean exists = this.service.existsBySlug(this.tag.getSlug());

        assertThat(exists).isTrue();

        verify(repository, times(1)).existsBySlugIgnoreCase(this.tag.getSlug());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnFalseWhenCheckExistsTagBySlug() {
        when(repository.existsBySlugIgnoreCase(this.tag.getSlug())).thenReturn(false);

        Boolean exists = this.service.existsBySlug(this.tag.getSlug());

        assertThat(exists).isFalse();

        verify(repository, times(1)).existsBySlugIgnoreCase(this.tag.getSlug());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldDeleteTag() {
        doNothing().when(repository).delete(tag);

        this.service.delete(tag);

        verify(repository, times(1)).delete(this.tag);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldCreateNewTag() {
        CreateTagDTO dto = new CreateTagDTO(
            this.tag.getName(),
            this.tag.getSlug(),
            this.tag.getDescription(),
            this.tag.getIsActive(),
            this.tag.getIsVisible(),
            this.tag.getIsSystem()
        );

        when(repository.save(any(TagModel.class))).thenReturn(this.tag);
        when(generator.nextId()).thenReturn(this.tag.getId());
        when(mapper.toModel(dto)).thenReturn(this.tag);

        TagModel model = this.service.create(dto);

        assertThat(model).isEqualTo(this.tag);

        verify(repository, times(1)).save(any(TagModel.class));
        verifyNoMoreInteractions(repository);

        verify(generator, times(1)).nextId();
        verify(mapper, times(1)).toModel(dto);
    }

    @Test
    @DisplayName("Should update all tag parameters and save them.")
    void shouldUpdateAllTagParams() {
        TagModel tagToUpdate = tag.toBuilder().build();
        OffsetDateTime now = OffsetDateTime.now();

        UpdateTagDTO updateDTO = new UpdateTagDTO(
                1L,
                "Novo Nome",
                "novo-slug",
                "Nova Descrição",
                false,
                false,
                true,
                now
        );

        doAnswer(invocation -> {
            TagModel model = invocation.getArgument(1);
            model.setName(updateDTO.name());
            model.setSlug(updateDTO.slug());
            model.setDescription(updateDTO.description());
            model.setIsActive(updateDTO.isActive());
            model.setIsVisible(updateDTO.isVisible());
            model.setIsSystem(updateDTO.isSystem());
            model.setLastUsedAt(updateDTO.lastUsedAt());
            return null;
        }).when(mapper).merge(eq(updateDTO), eq(tagToUpdate));

        when(repository.save(any(TagModel.class))).thenReturn(tagToUpdate);

        TagModel result = service.update(updateDTO, tagToUpdate);

        verify(mapper, times(1)).merge(eq(updateDTO), eq(tagToUpdate));
        verify(repository, times(1)).save(eq(tagToUpdate));

        assertEquals("Novo Nome", result.getName());
        assertEquals("novo-slug", result.getSlug());
        assertFalse(result.getIsActive());
        assertEquals(now, result.getLastUsedAt());
    }

    @Test
    @DisplayName("Should update only the tag name.")
    void shouldUpdateJustTagName() {
        TagModel tagToUpdate = tag.toBuilder().build();
        String oldSlug = tagToUpdate.getSlug();
        UpdateTagDTO updateDTO = new UpdateTagDTO(1L, "Java 21", null, null, null, null, null, null);

        doAnswer(invocation -> {
            TagModel model = invocation.getArgument(1);
            model.setName(updateDTO.name());
            return null;
        }).when(mapper).merge(eq(updateDTO), eq(tagToUpdate));

        when(repository.save(any(TagModel.class))).thenReturn(tagToUpdate);

        TagModel result = service.update(updateDTO, tagToUpdate);

        assertEquals("Java 21", result.getName());
        assertEquals(oldSlug, result.getSlug());
        verify(repository).save(tagToUpdate);
    }

    @Test
    @DisplayName("Should update only the isSystem flag.")
    void shouldUpdateJustIsSystem() {
        TagModel tagToUpdate = tag.toBuilder().build();
        UpdateTagDTO updateDTO = new UpdateTagDTO(1L, null, null, null, null, null, true, null);

        doAnswer(invocation -> {
            TagModel model = invocation.getArgument(1);
            model.setIsSystem(updateDTO.isSystem());
            return null;
        }).when(mapper).merge(eq(updateDTO), eq(tagToUpdate));

        when(repository.save(any(TagModel.class))).thenReturn(tagToUpdate);

        TagModel result = service.update(updateDTO, tagToUpdate);

        assertTrue(result.getIsSystem());
        assertEquals(this.tag.getName(), result.getName());
        verify(repository).save(tagToUpdate);
    }

    @Test
    @DisplayName("Should update only the lastUsedAt field.")
    void shouldUpdateJustLastUsedAt() {
        TagModel tagToUpdate = tag.toBuilder().build();
        OffsetDateTime newTime = OffsetDateTime.now().plusDays(1);
        UpdateTagDTO updateDTO = new UpdateTagDTO(1L, null, null, null, null, null, null, newTime);

        doAnswer(invocation -> {
            TagModel model = invocation.getArgument(1);
            model.setLastUsedAt(updateDTO.lastUsedAt());
            return null;
        }).when(mapper).merge(eq(updateDTO), eq(tagToUpdate));

        when(repository.save(any(TagModel.class))).thenReturn(tagToUpdate);

        // Act
        TagModel result = service.update(updateDTO, tagToUpdate);

        // Assert
        assertEquals(newTime, result.getLastUsedAt());
        verify(repository).save(tagToUpdate);
    }

    @Test
    @DisplayName("Should return the tag without changes if DTO fields are null.")
    void shouldReturnSameTagIfDtoIsEmpty() {
        TagModel tagToUpdate = tag.toBuilder().build();
        String originalName = tagToUpdate.getName();
        UpdateTagDTO updateDTO = new UpdateTagDTO(1L, null, null, null, null, null, null, null);

        when(repository.save(any(TagModel.class))).thenReturn(tagToUpdate);

        TagModel result = service.update(updateDTO, tagToUpdate);

        verify(mapper).merge(eq(updateDTO), eq(tagToUpdate));
        assertEquals(originalName, result.getName());
    }

}
