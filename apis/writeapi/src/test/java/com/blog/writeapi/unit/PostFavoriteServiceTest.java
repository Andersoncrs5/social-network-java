package com.blog.writeapi.unit;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.postFavorite.gateway.PostFavoriteModuleGateway;
import com.blog.writeapi.modules.postFavorite.models.PostFavoriteModel;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.enums.Post.PostStatusEnum;
import com.blog.writeapi.modules.postFavorite.repository.PostFavoriteRepository;
import com.blog.writeapi.modules.postFavorite.service.providers.PostFavoriteService;
import com.blog.writeapi.utils.exceptions.BusinessRuleException;
import com.blog.writeapi.utils.exceptions.InternalServerErrorException;
import com.blog.writeapi.utils.exceptions.UniqueConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostFavoriteServiceTest {

    @Mock private PostFavoriteRepository repository;
    @Mock private Snowflake generator;
    @Mock private PostFavoriteModuleGateway gateway;

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
    @DisplayName("Should create post favorite successfully")
    void shouldCreatePostFavorite() {
        when(generator.nextId()).thenReturn(this.favorite.getId());
        when(repository.save(any(PostFavoriteModel.class))).thenReturn(this.favorite);

        PostFavoriteModel model = this.service.create(post, user);

        assertThat(model.getId()).isEqualTo(this.favorite.getId());

        InOrder inOrder = inOrder(generator, repository);
        inOrder.verify(generator).nextId();
        inOrder.verify(repository).save(any(PostFavoriteModel.class));

        verifyNoMoreInteractions(generator, repository, gateway);
    }

    @Test
    @DisplayName("Should throw BusinessRuleException when user is blocked")
    void shouldThrowBusinessRuleException_WhenUserIsBlocked() {
        UserModel author = UserModel.builder().id(999L).build();
        PostModel postWithDifferentAuthor = post.toBuilder().author(author).build();

        when(gateway.isBlocked(user.getId(), author.getId())).thenReturn(true);

        assertThatThrownBy(() -> service.create(postWithDifferentAuthor, user))
                .isInstanceOf(BusinessRuleException.class);

        verify(gateway).isBlocked(user.getId(), author.getId());
        verifyNoInteractions(repository, generator);
    }

    @Test
    @DisplayName("Should throw UniqueConstraintViolationException when already favorited")
    void shouldThrowUniqueConstraintViolationException_WhenAlreadyFavorited() {
        when(generator.nextId()).thenReturn(this.favorite.getId());

        var exception = new DataIntegrityViolationException("Conflict", new RuntimeException("uk_posts_favorites"));
        when(repository.save(any(PostFavoriteModel.class))).thenThrow(exception);

        assertThatThrownBy(() -> service.create(post, user))
                .isInstanceOf(UniqueConstraintViolationException.class);

        verify(repository).save(any(PostFavoriteModel.class));
    }

    @Test
    @DisplayName("Should throw InternalServerErrorException when unexpected error occurs")
    void shouldThrowInternalServerErrorException_WhenGenericError() {
        when(generator.nextId()).thenReturn(this.favorite.getId());
        when(repository.save(any())).thenThrow(new RuntimeException("Unexpected error"));

        assertThatThrownBy(() -> service.create(post, user))
                .isInstanceOf(InternalServerErrorException.class)
                .hasMessage("Error adding post to favorite");

        verify(repository).save(any());
    }

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
