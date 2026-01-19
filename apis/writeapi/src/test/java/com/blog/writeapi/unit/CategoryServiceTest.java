package com.blog.writeapi.unit;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.dtos.category.CreateCategoryDTO;
import com.blog.writeapi.dtos.category.UpdateCategoryDTO;
import com.blog.writeapi.models.CategoryModel;
import com.blog.writeapi.repositories.CategoryRepository;
import com.blog.writeapi.services.providers.CategoryService;
import com.blog.writeapi.utils.mappers.CategoryMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock private CategoryRepository repository;
    @Mock private Snowflake generator;

    @InjectMocks private CategoryService service;
    @Mock private CategoryMapper mapper;


    CategoryModel category = new CategoryModel().toBuilder()
            .id(1998780200074176609L)
            .name("TI")
            .description("Any Desc")
            .slug("ti")
            .isActive(true)
            .visible(true)
            .displayOrder(1)
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    @Test
    void shouldGetCategoryById() {
        when(repository.findById(category.getId())).thenReturn(Optional.of(this.category));

        Optional<CategoryModel> optional = this.service.getById(category.getId());

        assertThat(optional.isPresent()).isTrue();
        assertThat(optional.get()).isEqualTo(category);

        verify(repository, times(1)).findById(category.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnNullWhenGetCategoryById() {
        when(repository.findById(category.getId())).thenReturn(Optional.empty());

        Optional<CategoryModel> optional = this.service.getById(category.getId());

        assertThat(optional.isEmpty()).isTrue();

        verify(repository, times(1)).findById(category.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldDeleteCategory() {
        doNothing().when(repository).delete(this.category);

        this.service.delete(category);

        verify(repository, times(1)).delete(this.category);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnTrueWhenExecMethodExistsByName() {
        when(repository.existsByNameIgnoreCase("")).thenReturn(true);

        Boolean exists = this.service.existsByName("");

        assertThat(exists).isTrue();

        verify(repository, times(1)).existsByNameIgnoreCase("");
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnFalseWhenExecMethodExistsByName() {
        when(repository.existsByNameIgnoreCase("")).thenReturn(false);

        Boolean exists = this.service.existsByName("");

        assertThat(exists).isFalse();

        verify(repository, times(1)).existsByNameIgnoreCase("");
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnTrueWhenExecMethodExistsById() {
        when(repository.existsById(1L)).thenReturn(true);

        Boolean exists = this.service.existsById(1L);

        assertThat(exists).isTrue();

        verify(repository, times(1)).existsById(1L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnFalseWhenExecMethodExistsById() {
        when(repository.existsById(1L)).thenReturn(false);

        Boolean exists = this.service.existsById(1L);

        assertThat(exists).isFalse();

        verify(repository, times(1)).existsById(1L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldGetCategoryByName() {
        when(repository.findByNameIgnoreCase(category.getName())).thenReturn(Optional.of(this.category));

        Optional<CategoryModel> optional = this.service.getByName(category.getName());

        assertThat(optional.isPresent()).isTrue();
        assertThat(optional.get()).isEqualTo(category);

        verify(repository, times(1)).findByNameIgnoreCase(category.getName());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnNullWhenGetCategoryByName() {
        when(repository.findByNameIgnoreCase(category.getName())).thenReturn(Optional.empty());

        Optional<CategoryModel> optional = this.service.getByName(category.getName());

        assertThat(optional.isEmpty()).isTrue();

        verify(repository, times(1)).findByNameIgnoreCase(category.getName());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldGetCategoryBySlug() {
        when(repository.findBySlug(category.getSlug())).thenReturn(Optional.of(this.category));

        Optional<CategoryModel> optional = this.service.getBySlug(category.getSlug());

        assertThat(optional.isPresent()).isTrue();
        assertThat(optional.get()).isEqualTo(category);

        verify(repository, times(1)).findBySlug(category.getSlug());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnNullWhenGetCategoryBySlug() {
        when(repository.findBySlug(category.getSlug())).thenReturn(Optional.empty());

        Optional<CategoryModel> optional = this.service.getBySlug(category.getSlug());

        assertThat(optional.isEmpty()).isTrue();

        verify(repository, times(1)).findBySlug(category.getSlug());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnTrueWhenExecMethodExistsBySlug() {
        when(repository.existsBySlug("")).thenReturn(true);

        Boolean exists = this.service.existsBySlug("");

        assertThat(exists).isTrue();

        verify(repository, times(1)).existsBySlug("");
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnFalseWhenExecMethodExistsBySlug() {
        when(repository.existsBySlug("")).thenReturn(false);

        Boolean exists = this.service.existsBySlug("");

        assertThat(exists).isFalse();

        verify(repository, times(1)).existsBySlug("");
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldCreateCategory() {
        CategoryModel categoryCopy = new CategoryModel().toBuilder()
                .name(this.category.getName())
                .description(this.category.getDescription())
                .slug(this.category.getSlug())
                .isActive(this.category.getIsActive())
                .visible(this.category.getIsActive())
                .displayOrder(this.category.getDisplayOrder())
                .version(this.category.getVersion())
                .createdAt(this.category.getCreatedAt())
                .updatedAt(this.category.getUpdatedAt())
                .build();

        CreateCategoryDTO dto = new CreateCategoryDTO(
                this.category.getName(),
                this.category.getDescription(),
                this.category.getSlug(),
                this.category.getIsActive(),
                this.category.getVisible(),
                this.category.getDisplayOrder(),
                null
        );

        when(generator.nextId()).thenReturn(category.getId());
        when(repository.save(categoryCopy)).thenReturn(category);
        when(mapper.toModel(dto)).thenReturn(categoryCopy);

        CategoryModel categoryModel = this.service.create(dto);

        assertThat(categoryModel).isEqualTo(this.category);

        verify(repository, times(1)).save(categoryCopy);
        verify(generator, times(1)).nextId();

        verifyNoMoreInteractions(repository);
        verifyNoMoreInteractions(generator);

        InOrder inOrder = inOrder(repository, generator, mapper);

        inOrder.verify(mapper).toModel(dto);
        inOrder.verify(generator).nextId();
        inOrder.verify(repository).save(categoryCopy);
    }

    @Test
    @DisplayName("You must update all parameters and save them to the repository.")
    void shouldUpdateAllParams() {
        CategoryModel categoryToUpdate = category.toBuilder().build();

        UpdateCategoryDTO updateDTO = new UpdateCategoryDTO(
                1L,
                "Inovação",
                "Nova Descrição",
                "inovacao-slug",
                false,
                false,
                10,
                null,
                null
        );

        doAnswer(invocation -> {
            CategoryModel cat = invocation.getArgument(1);
            cat.setName(updateDTO.name());
            cat.setDescription(updateDTO.description());
            cat.setSlug(updateDTO.slug());
            cat.setIsActive(updateDTO.isActive());
            cat.setVisible(updateDTO.visible());
            cat.setDisplayOrder(updateDTO.displayOrder());
            return null;
        }).when(mapper).merge(eq(updateDTO), eq(categoryToUpdate));

        when(repository.save(any(CategoryModel.class))).thenReturn(categoryToUpdate);

        CategoryModel result = service.update(updateDTO, categoryToUpdate);

        verify(mapper, times(1)).merge(eq(updateDTO), eq(categoryToUpdate));
        verify(repository, times(1)).save(eq(categoryToUpdate));

        assertEquals("Inovação", result.getName());
        assertEquals(10, result.getDisplayOrder());
    }

    @Test
    @DisplayName("You only need to update the 'name' field.")
    void shouldUpdateJustName() {
        CategoryModel categoryToUpdate = category.toBuilder().build();

        UpdateCategoryDTO updateDTO = new UpdateCategoryDTO(
                1L,
                "Design UX/UI",
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        String oldSlug = categoryToUpdate.getSlug();

        doAnswer(invocation -> {
            CategoryModel cat = invocation.getArgument(1);
            cat.setName(updateDTO.name());
            return null;
        }).when(mapper).merge(eq(updateDTO), eq(categoryToUpdate));

        when(repository.save(any(CategoryModel.class))).thenReturn(categoryToUpdate);

        CategoryModel result = service.update(updateDTO, categoryToUpdate);

        verify(repository, times(1)).save(eq(categoryToUpdate));

        assertEquals("Design UX/UI", result.getName());
        assertEquals(oldSlug, result.getSlug());
    }

    @Test
    @DisplayName("You only need to update the 'description' field.")
    void shouldUpdateJustDescription() {
        CategoryModel categoryToUpdate = category.toBuilder().build();
        UpdateCategoryDTO updateDTO = new UpdateCategoryDTO(
                null,
                null,
                "Descrição detalhada do tema",
                null,
                null,
                null,
                null,
                null,
                null
        );

        doAnswer(invocation -> {
            CategoryModel cat = invocation.getArgument(1);
            cat.setDescription(updateDTO.description());
            return null;
        }).when(mapper).merge(eq(updateDTO), eq(categoryToUpdate));

        when(repository.save(any(CategoryModel.class))).thenReturn(categoryToUpdate);

        CategoryModel result = service.update(updateDTO, categoryToUpdate);

        verify(repository, times(1)).save(eq(categoryToUpdate));
        assertEquals("Descrição detalhada do tema", result.getDescription());
        assertEquals("TI", result.getName());
    }

    @Test
    @DisplayName("You only need to update the 'slug' field.")
    void shouldUpdateJustSlug() {
        CategoryModel categoryToUpdate = category.toBuilder().build();

        UpdateCategoryDTO updateDTO = new UpdateCategoryDTO(
                null,
                null,
                null,
                "novo-slug-unico",
                null,
                null,
                null,
                null,
                null
        );

        doAnswer(invocation -> {
            CategoryModel cat = invocation.getArgument(1);
            cat.setSlug(updateDTO.slug());
            return null;
        }).when(mapper).merge(eq(updateDTO), eq(categoryToUpdate));

        when(repository.save(any(CategoryModel.class))).thenReturn(categoryToUpdate);

        CategoryModel result = service.update(updateDTO, categoryToUpdate);

        verify(repository, times(1)).save(eq(categoryToUpdate));
        assertEquals("novo-slug-unico", result.getSlug());
    }

    @Test
    @DisplayName("You must update the 'isActive' status to false.")
    void shouldUpdateJustIsActive() {
        CategoryModel categoryToUpdate = category.toBuilder().build();

        UpdateCategoryDTO updateDTO = new UpdateCategoryDTO(
                null, null, null, null, false, null, null, null, null
        );

        doAnswer(invocation -> {
            CategoryModel cat = invocation.getArgument(1);
            cat.setIsActive(updateDTO.isActive());
            return null;
        }).when(mapper).merge(eq(updateDTO), eq(categoryToUpdate));

        when(repository.save(any(CategoryModel.class))).thenReturn(categoryToUpdate);

        CategoryModel result = service.update(updateDTO, categoryToUpdate);

        verify(repository, times(1)).save(eq(categoryToUpdate));
        assertEquals(false, result.getIsActive());
    }

    @Test
    @DisplayName("You need to update 'displayOrder'.")
    void shouldUpdateJustDisplayOrder() {
        CategoryModel categoryToUpdate = category.toBuilder().build();

        UpdateCategoryDTO updateDTO = new UpdateCategoryDTO(
                null, null, null, null, null, null, 99, null, null
        );

        doAnswer(invocation -> {
            CategoryModel cat = invocation.getArgument(1);
            cat.setDisplayOrder(updateDTO.displayOrder());
            return null;
        }).when(mapper).merge(eq(updateDTO), eq(categoryToUpdate));

        when(repository.save(any(CategoryModel.class))).thenReturn(categoryToUpdate);

        CategoryModel result = service.update(updateDTO, categoryToUpdate);

        verify(repository, times(1)).save(eq(categoryToUpdate));
        assertEquals(99, result.getDisplayOrder());
    }

    @Test
    @DisplayName("It should return the entity if the DTO is entirely null (no changes).")
    void shouldReturnSameCategoryIfAllDtoFieldsAreNull() {
        CategoryModel categoryToUpdate = category.toBuilder().build();

        UpdateCategoryDTO updateDTO = new UpdateCategoryDTO(
                null, null, null, null, null, null, null, null, null
        );

        when(repository.save(eq(categoryToUpdate))).thenReturn(categoryToUpdate);

        CategoryModel result = service.update(updateDTO, categoryToUpdate);

        verify(mapper, times(1)).merge(eq(updateDTO), eq(categoryToUpdate));
        verify(repository, times(1)).save(eq(categoryToUpdate));

        assertEquals("TI", result.getName());
    }

}
