package com.blog.writeapi.unit;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.postFavorite.models.PostFavoriteModel;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.enums.Post.PostStatusEnum;
import com.blog.writeapi.modules.postFavorite.repository.PostFavoriteRepository;
import com.blog.writeapi.modules.postFavorite.service.providers.PostFavoriteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostFavoriteServiceTest {

    @Mock private PostFavoriteRepository repository;
    @Mock private Snowflake generator;

    @InjectMocks private PostFavoriteService service;

    UserModel user = UserModel.builder()
            .id(1998780200074176609L)
            .name("user")
            .email("user@gmail.com")
            .password("12345678")
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
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
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    PostFavoriteModel favorite = new PostFavoriteModel().toBuilder()
            .id(1998180103274176609L)
            .post(this.post)
            .user(this.user)
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    @Test
    void shouldGetPostFavorite() {
        when(repository.findById(this.favorite.getId())).thenReturn(Optional.of(this.favorite));

        PostFavoriteModel favorite = this.service.getByIdSimple(this.favorite.getId());

        assertThat(favorite.getId()).isEqualTo(this.favorite.getId());

        verify(repository, times(1)).findById(this.favorite.getId());
        verifyNoMoreInteractions(repository);
    }

    // DELETE
    @Test
    void shouldDeletePostFavorite() {
        doNothing().when(repository).delete(this.favorite);

        this.service.delete(this.favorite);

        verify(repository, times(1)).delete(this.favorite);
        verifyNoMoreInteractions(repository);
    }

    // CREATE
    @Test
    void shouldCreatePostFavorite() {
        when(generator.nextId()).thenReturn(this.favorite.getId());
        when(repository.save(this.favorite)).thenReturn(this.favorite);

        PostFavoriteModel model = this.service.create(post, user);

        assertThat(model).isEqualTo(this.favorite);

        verify(repository, times(1)).save(this.favorite);
        verify(generator, times(1)).nextId();

        InOrder inOrder = inOrder(generator, repository);

        inOrder.verify(generator).nextId();
        inOrder.verify(repository).save(this.favorite);

        verifyNoMoreInteractions(generator, repository);
    }

    // ExistsByPostAndUser
    @Test
    void shouldReturnTrueWhenExistsByPostAndUser() {
        when(repository.existsByPostAndUser(this.post, this.user)).thenReturn(true);

        Boolean exists = this.service.existsByPostAndUser(this.post, this.user);

        assertThat(exists).isTrue();

        verify(repository, times(1)).existsByPostAndUser(this.post, this.user);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnFalseWhenExistsByPostAndUser() {
        when(repository.existsByPostAndUser(this.post, this.user)).thenReturn(false);

        Boolean exists = this.service.existsByPostAndUser(this.post, this.user);

        assertThat(exists).isFalse();

        verify(repository, times(1)).existsByPostAndUser(this.post, this.user);
        verifyNoMoreInteractions(repository);
    }


}
