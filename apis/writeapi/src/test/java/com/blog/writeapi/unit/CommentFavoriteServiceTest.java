package com.blog.writeapi.unit;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.commentFavorite.gateway.CommentFavoriteModuleGateway;
import com.blog.writeapi.modules.commentFavorite.models.CommentFavoriteModel;
import com.blog.writeapi.modules.comment.models.CommentModel;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.enums.Post.PostStatusEnum;
import com.blog.writeapi.utils.enums.comment.CommentStatusEnum;
import com.blog.writeapi.modules.commentFavorite.repository.CommentFavoriteRepository;
import com.blog.writeapi.modules.commentFavorite.service.providers.CommentFavoriteService;
import com.blog.writeapi.utils.exceptions.BusinessRuleException;
import com.blog.writeapi.utils.exceptions.InternalServerErrorException;
import com.blog.writeapi.utils.exceptions.UniqueConstraintViolationException;
import com.blog.writeapi.utils.mappers.CommentFavoriteMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
public class CommentFavoriteServiceTest {

    @Mock private CommentFavoriteRepository repository;
    @Mock private Snowflake generator;
    @Mock private CommentFavoriteMapper mapper;
    @Mock private CommentFavoriteModuleGateway gateway;

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

    @Test
    @DisplayName("Should throw BusinessRuleException when user is blocked")
    void shouldThrowBusinessRuleException_WhenUserIsBlocked() {
        UserModel author = UserModel.builder().id(999L).build();
        CommentModel commentFromOther = comment.toBuilder().author(author).build();

        when(gateway.isBlocked(user.getId(), author.getId())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> service.add(user, commentFromOther))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("You cannot favorite a comment from a blocked user.");

        verify(gateway).isBlocked(user.getId(), author.getId());
        verifyNoInteractions(repository, generator);
    }

    @Test
    @DisplayName("Should throw UniqueConstraintViolationException when already favorited")
    void shouldThrowUniqueConstraintViolationException_WhenDuplicated() {
        when(generator.nextId()).thenReturn(favorite.getId());

        var exception = new DataIntegrityViolationException("Conflict", new RuntimeException("uk_comments_favorites"));
        when(repository.save(any(CommentFavoriteModel.class))).thenThrow(exception);

        assertThatThrownBy(() -> service.add(user, comment))
                .isInstanceOf(UniqueConstraintViolationException.class)
                .hasMessage("This comment is already in your favorites.");

        verify(repository).save(any(CommentFavoriteModel.class));
    }

    @Test
    @DisplayName("Should throw InternalServerErrorException on unexpected error")
    void shouldThrowInternalServerErrorException_WhenGenericError() {
        when(generator.nextId()).thenReturn(favorite.getId());
        when(repository.save(any())).thenThrow(new RuntimeException("DB Crash"));

        assertThatThrownBy(() -> service.add(user, comment))
                .isInstanceOf(InternalServerErrorException.class)
                .hasMessage("Error processing your request.");

        verify(repository).save(any());
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
