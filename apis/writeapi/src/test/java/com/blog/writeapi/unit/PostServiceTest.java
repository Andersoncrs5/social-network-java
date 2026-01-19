package com.blog.writeapi.unit;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.dtos.post.CreatePostDTO;
import com.blog.writeapi.models.PostModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.models.enums.Post.PostStatusEnum;
import com.blog.writeapi.repositories.PostRepository;
import com.blog.writeapi.services.providers.PostService;
import com.blog.writeapi.utils.mappers.PostMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock private PostRepository repository;
    @Mock private Snowflake generator;
    @Mock private PostMapper mapper;

    @InjectMocks private PostService service;

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

    @Test
    void shouldReturnPostWhenGetById() {
        when(repository.findById(this.post.getId())).thenReturn(Optional.of(this.post));

        Optional<PostModel> optional = this.service.getById(this.post.getId());

        assertThat(optional.isPresent()).isTrue();
        assertThat(optional.get().getId()).isEqualTo(this.post.getId());

        verify(repository, times(1)).findById(this.post.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnNullWhenGetById() {
        when(repository.findById(this.post.getId())).thenReturn(Optional.empty());

        Optional<PostModel> optional = this.service.getById(this.post.getId());

        assertThat(optional.isPresent()).isFalse();

        verify(repository, times(1)).findById(this.post.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnTrueWhenExistsById() {
        when(repository.existsById(this.post.getId())).thenReturn(true);

        Boolean exists = this.service.existsById(this.post.getId());

        assertThat(exists).isTrue();

        verify(repository, times(1)).existsById(this.post.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnFalseWhenExistsById() {
        when(repository.existsById(this.post.getId())).thenReturn(false);

        Boolean exists = this.service.existsById(this.post.getId());

        assertThat(exists).isFalse();

        verify(repository, times(1)).existsById(this.post.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnTrueWhenExistsBySlug() {
        when(repository.existsBySlugIgnoreCase(this.post.getSlug())).thenReturn(true);

        Boolean exists = this.service.existsBySlug(this.post.getSlug());

        assertThat(exists).isTrue();

        verify(repository, times(1)).existsBySlugIgnoreCase(this.post.getSlug());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnFalseWhenExistsBySlug() {
        when(repository.existsBySlugIgnoreCase(this.post.getSlug())).thenReturn(false);

        Boolean exists = this.service.existsBySlug(this.post.getSlug());

        assertThat(exists).isFalse();

        verify(repository, times(1)).existsBySlugIgnoreCase(this.post.getSlug());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnPostWheFindBySlug() {
        when(repository.findBySlugIgnoreCase(this.post.getSlug())).thenReturn(Optional.of(this.post));

        Optional<PostModel> optional = this.service.getBySlug(this.post.getSlug());

        assertThat(optional.isPresent()).isTrue();
        assertThat(optional.get().getSlug()).isEqualTo(this.post.getSlug());

        verify(repository, times(1)).findBySlugIgnoreCase(this.post.getSlug());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnNullWheFindBySlug() {
        when(repository.findBySlugIgnoreCase(this.post.getSlug())).thenReturn(Optional.empty());

        Optional<PostModel> optional = this.service.getBySlug(this.post.getSlug());

        assertThat(optional.isPresent()).isFalse();

        verify(repository, times(1)).findBySlugIgnoreCase(this.post.getSlug());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldDeletePost() {
        doNothing().when(repository).delete(this.post);

        this.service.delete(this.post);

        verify(repository, times(1)).delete(this.post);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldCreatePost() {
        CreatePostDTO dto = new CreatePostDTO(
                this.post.getTitle(),
                this.post.getSlug(),
                this.post.getContent(),
                this.post.getReadingTime()
        );

        when(repository.save(any())).thenReturn(this.post);
        when(mapper.toModel(dto)).thenReturn(this.post);
        when(this.generator.nextId()).thenReturn(this.post.getId());

        PostModel model = this.service.create(dto, this.user);
        assertThat(model).isEqualTo(this.post);

        verify(repository, times(1)).save(any());
        verifyNoMoreInteractions(repository);

        verify(this.mapper, times(1)).toModel(dto);
        verifyNoMoreInteractions(this.mapper);

        verify(generator, times(1)).nextId();
        verifyNoMoreInteractions(this.generator);
    }

}
