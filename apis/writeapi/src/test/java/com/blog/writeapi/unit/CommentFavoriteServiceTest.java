package com.blog.writeapi.unit;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.models.CommentFavoriteModel;
import com.blog.writeapi.models.CommentModel;
import com.blog.writeapi.models.PostModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.models.enums.Post.PostStatusEnum;
import com.blog.writeapi.models.enums.comment.CommentStatusEnum;
import com.blog.writeapi.repositories.CommentFavoriteRepository;
import com.blog.writeapi.services.providers.CommentFavoriteService;
import com.blog.writeapi.utils.mappers.CommentFavoriteMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentFavoriteServiceTest {

    @Mock private CommentFavoriteRepository repository;
    @Mock private Snowflake generator;
    @Mock private CommentFavoriteMapper mapper;

    @InjectMocks private CommentFavoriteService service;

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

    CommentModel comment = new CommentModel().toBuilder()
            .id(1998780200074176609L)
            .content("content")
            .status(CommentStatusEnum.APPROVED)
            .post(this.post)
            .author(this.user)
            .parent(null)
            .edited(true)
            .pinned(true)
            .ipAddress("ip-45743567346")
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    CommentFavoriteModel favorite = new CommentFavoriteModel().toBuilder()
            .id(1998780200994176609L)
            .comment(this.comment)
            .user(this.user)
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    // METHOD: add
    @Test
    public void shouldCreateCommentFavorite() {
        when(this.generator.nextId()).thenReturn(this.favorite.getId());
        when(this.repository.save(this.favorite)).thenReturn(this.favorite);

        CommentFavoriteModel model = this.service.add(this.user, this.comment);
        assertThat(model).isEqualTo(this.favorite);

        verify(this.generator, times(1)).nextId();
        verify(this.repository, times(1)).save(this.favorite);

        verifyNoMoreInteractions(repository);
        verifyNoMoreInteractions(generator);
    }

    // METHOD: getById
    @Test void shouldGetCommentFavoriteById() {
        when(repository.findById(this.favorite.getId())).thenReturn(Optional.of(this.favorite));

        CommentFavoriteModel favor = this.service.getById(this.favorite.getId());

        assertThat(favor).isEqualTo(this.favorite);

        verify(repository, times(1)).findById(this.favorite.getId());
        verifyNoMoreInteractions(repository);
    }

    // METHOD: delete
    @Test void shouldDeleteCommentFavorite() {
        doNothing().when(repository).delete(this.favorite);

        this.service.remove(this.favorite);

        verify(repository, times(1)).delete(this.favorite);
        verifyNoMoreInteractions(repository);
    }

}
