package com.blog.writeapi.unit;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.dtos.postCategories.CreatePostCategoriesDTO;
import com.blog.writeapi.models.CategoryModel;
import com.blog.writeapi.models.PostCategoriesModel;
import com.blog.writeapi.models.PostModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.models.enums.Post.PostStatusEnum;
import com.blog.writeapi.repositories.PostCategoriesRepository;
import com.blog.writeapi.services.providers.PostCategoriesService;
import com.blog.writeapi.utils.mappers.PostCategoriesMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostCategoriesServiceTest {

    @Mock private PostCategoriesRepository repository;
    @Mock private Snowflake generator;
    @Mock private PostCategoriesMapper mapper;

    @InjectMocks private PostCategoriesService service;

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

    UserModel user = UserModel.builder()
            .id(1998780200074176609L)
            .name("user")
            .email("user@gmail.com")
            .password("12345678")
            .build();

    PostModel post = new PostModel().toBuilder()
            .id(1998780203274176609L)
            .title("anyTittle")
            .slug("any-title")
            .content("any Content")
            .status(PostStatusEnum.PUBLISHED)
            .readingTime(5)
            .rankingScore(0.0)
            .isFeatured(false)
            .author(user)
            .build();

    PostCategoriesModel postCategories = new PostCategoriesModel().toBuilder()
            .id(1998780209974176609L)
            .post(this.post)
            .category(this.category)
            .displayOrder(1)
            .primary(true)
            .active(true)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    @Test
    void shouldReturnPostCategoriesWhenGetById() {
        when(repository.findById(this.postCategories.getId())).thenReturn(Optional.of(this.postCategories));

        Optional<PostCategoriesModel> optional = this.service.getById(this.postCategories.getId());

        assertThat(optional.isPresent()).isTrue();

        verify(repository, times(1)).findById(this.postCategories.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnNullWhenGetById() {
        when(repository.findById(this.postCategories.getId())).thenReturn(Optional.empty());

        Optional<PostCategoriesModel> optional = this.service.getById(this.postCategories.getId());

        assertThat(optional.isEmpty()).isTrue();

        verify(repository, times(1)).findById(this.postCategories.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldDeletePostCategory() {
        doNothing().when(repository).delete(this.postCategories);

        this.service.delete(this.postCategories);

        verify(repository, times(1)).delete(this.postCategories);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldAddCategoryToPost() {
        CreatePostCategoriesDTO dto = new CreatePostCategoriesDTO(
                this.post.getId(),
                this.category.getId(),
                6,
                true,
                true
        );

        PostCategoriesModel mappedModel = new PostCategoriesModel();

        when(mapper.toModel(dto)).thenReturn(mappedModel);
        when(generator.nextId()).thenReturn(this.postCategories.getId());

        when(repository.save(any(PostCategoriesModel.class))).thenReturn(this.postCategories);

        PostCategoriesModel result = this.service.create(dto, post, category);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(this.postCategories.getId());
        assertThat(result.getPost()).isEqualTo(this.post);

        verify(mapper).toModel(dto);
        verify(generator).nextId();
        verify(repository).save(any(PostCategoriesModel.class));
    }



}
