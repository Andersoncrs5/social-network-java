package com.blog.writeapi.unit;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.models.*;
import com.blog.writeapi.models.enums.Post.PostStatusEnum;
import com.blog.writeapi.models.enums.comment.CommentStatusEnum;
import com.blog.writeapi.repositories.CommentReactionRepository;
import com.blog.writeapi.services.providers.CommentReactionService;
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
public class CommentReactionServiceTest {
    @Mock private Snowflake generator;
    @Mock private CommentReactionRepository repository;

    @InjectMocks private CommentReactionService service;

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
            .id(1998780200074276609L)
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

    ReactionModel reaction = new ReactionModel().toBuilder()
            .id(1998780200074176609L)
            .name("HEART")
            .emojiUrl("https://www.shutterstock.com/shutterstock/photos/2276851457/display_1500/stock-vector-beating-heart-emoji-isolated-on-white-background-emoticons-symbol-modern-simple-vector-printed-2276851457.jpg")
            .emojiUnicode("4536465")
            .displayOrder(1L)
            .active(true)
            .visible(true)
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    CommentReactionModel commentReaction = new CommentReactionModel().toBuilder()
            .id(1998780200074176699L)
            .comment(comment)
            .reaction(reaction)
            .user(user)
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    @Test
    void shouldCreateNewCommentReactionModel() {
        when(generator.nextId()).thenReturn(commentReaction.getId());

        when(repository.save(any(CommentReactionModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CommentReactionModel result = service.create(comment, reaction, user);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(commentReaction.getId());
        assertThat(result.getComment()).isEqualTo(comment);
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getReaction()).isEqualTo(reaction);

        verify(generator, times(1)).nextId();
        verify(repository, times(1)).save(any(CommentReactionModel.class));

        verifyNoMoreInteractions(generator, repository);
    }

    @Test
    void shouldReturnCommentReactionFindByUserAndComment() {
        when(repository.findByUserAndComment(user, comment)).thenReturn(Optional.of(commentReaction));

        Optional<CommentReactionModel> optional = this.service.findByUserAndComment(user, comment);

        assertThat(optional.isPresent()).isTrue();

        verify(repository, times(1)).findByUserAndComment(user, comment);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnNullFindByUserAndComment() {
        when(repository.findByUserAndComment(user, comment)).thenReturn(Optional.empty());

        Optional<CommentReactionModel> optional = this.service.findByUserAndComment(user, comment);

        assertThat(optional.isEmpty()).isTrue();

        verify(repository, times(1)).findByUserAndComment(user, comment);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldDeleteCommentReaction() {
        doNothing().when(repository).delete(this.commentReaction);

        this.service.delete(this.commentReaction);

        verify(repository, times(1)).delete(this.commentReaction);
        verifyNoMoreInteractions(repository);
    }

}
