package com.blog.writeapi.unit;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.models.PostModel;
import com.blog.writeapi.models.PostReactionModel;
import com.blog.writeapi.models.ReactionModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.models.enums.Post.PostStatusEnum;
import com.blog.writeapi.repositories.PostReactionRepository;
import com.blog.writeapi.services.providers.PostReactionService;
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
public class PostReactionServiceTest {

    @Mock private PostReactionRepository repository;
    @Mock private Snowflake generator;

    @InjectMocks private PostReactionService service;

    UserModel user = UserModel.builder()
            .id(1998780200074176609L)
            .name("user")
            .email("user@gmail.com")
            .password("12345678")
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

    PostReactionModel postReaction = new PostReactionModel().toBuilder()
            .id(3998710203274176609L)
            .post(post)
            .user(user)
            .reaction(reaction)
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    @Test
    void shouldReturnPostReactionWhenExecMethodFindByPostAndUser() {
        when(repository.findByPostAndUser(post, user)).thenReturn(Optional.of(this.postReaction));

        Optional<PostReactionModel> optional = this.service.findByPostAndUser(post, user);

        assertThat(optional.isPresent()).isTrue();

        verify(repository, times(1)).findByPostAndUser(post, user);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnNullWhenExecMethodFindByPostAndUser() {
        when(repository.findByPostAndUser(post, user)).thenReturn(Optional.empty());

        Optional<PostReactionModel> optional = this.service.findByPostAndUser(post, user);

        assertThat(optional.isPresent()).isFalse();

        verify(repository, times(1)).findByPostAndUser(post, user);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldDeletePostReaction() {
        doNothing().when(repository).delete(this.postReaction);

        this.service.delete(this.postReaction);

        verify(repository, times(1)).delete(this.postReaction);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnTrueWhenExistsByPostAndUser() {
        when(repository.existsByPostAndUser(post, user)).thenReturn(true);

        Boolean exists = this.service.existsByPostAndUser(post, user);

        assertThat(exists).isTrue();

        verify(repository, times(1)).existsByPostAndUser(post, user);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnFalseWhenExistsByPostAndUser() {
        when(repository.existsByPostAndUser(post, user)).thenReturn(false);

        Boolean exists = this.service.existsByPostAndUser(post, user);

        assertThat(exists).isFalse();

        verify(repository, times(1)).existsByPostAndUser(post, user);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldCreateNewReaction() {
        when(generator.nextId()).thenReturn(postReaction.getId());
        when(repository.save(any(PostReactionModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PostReactionModel model = service.create(post, reaction, user);

        assertThat(model.getId()).isEqualTo(postReaction.getId());
        assertThat(model.getPost()).isEqualTo(post);
        assertThat(model.getReaction()).isEqualTo(reaction);

        verify(repository, times(1)).save(any(PostReactionModel.class)); // Use any() aqui
        verify(generator, times(1)).nextId();
    }

}
