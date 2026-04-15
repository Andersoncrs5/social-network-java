package com.blog.writeapi.unit.storyReaction;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.reaction.models.ReactionModel;
import com.blog.writeapi.modules.stories.model.StoryModel;
import com.blog.writeapi.modules.storyReaction.gateway.StoryReactionModuleGateway;
import com.blog.writeapi.modules.storyReaction.model.StoryReactionModel;
import com.blog.writeapi.modules.storyReaction.repository.StoryReactionRepository;
import com.blog.writeapi.modules.storyReaction.service.provider.StoryReactionService;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.classes.ResultToggle;
import com.blog.writeapi.utils.enums.attachment.AttachmentTypeEnum;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.exceptions.UniqueConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
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
public class StoryReactionServiceTest {

    @Mock
    private StoryReactionRepository repository;

    @Mock
    private StoryReactionModuleGateway gateway;

    @Mock
    private Snowflake generator;

    @InjectMocks
    private StoryReactionService service;

    UserModel user = new UserModel();
    StoryModel story = new StoryModel();
    ReactionModel reaction = new ReactionModel();
    StoryReactionModel storyReaction = new StoryReactionModel();
    Long userId;
    Long storyId;
    Long reactionId;

    @BeforeEach
    void setup() {
        user = UserModel.builder()
                .id(1998780200074176609L)
                .name("user")
                .email("user@gmail.com")
                .password("12345678")
                .version(1L)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        story = new StoryModel().toBuilder()
                .id(1111111111111111111L)
                .storageKey("key-111")
                .fileName("filename-111")
                .contentType("image/png")
                .type(AttachmentTypeEnum.IMAGE)
                .fileSize(2343L)
                .isPublic(true)
                .isVisible(true)
                .user(user)
                .expiresAt(OffsetDateTime.now().plusHours(24))
                .isArchived(false)
                .caption("caption")
                .backgroundColor("yellow")
                .version(1L)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        reaction = new ReactionModel().toBuilder()
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

        storyReaction = new StoryReactionModel().toBuilder()
                .id(1998780200074111111L)
                .user(user)
                .story(story)
                .reaction(reaction)
                .version(1L)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        this.userId = user.getId();
        this.storyId = story.getId();
        this.reactionId = reaction.getId();
    }

    @Test
    void shouldDelete() {

        doNothing().when(repository).delete(storyReaction);

        this.service.delete(storyReaction);

        verify(repository, times(1)).delete(storyReaction);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldCreate() {
        when(gateway.findReactionById(reactionId)).thenReturn(reaction);
        when(gateway.findStoryById(storyId)).thenReturn(story);
        when(gateway.findUserById(userId)).thenReturn(user);
        when(generator.nextId()).thenReturn(storyReaction.getId());
        when(repository.save(any())).thenReturn(storyReaction);

        StoryReactionModel model = this.service.create(userId, storyId, reactionId);

        assertThat(model).isNotNull().isEqualTo(storyReaction);

        InOrder order = inOrder(repository, generator, gateway);

        order.verify(gateway).findReactionById(reactionId);
        order.verify(gateway).findStoryById(storyId);
        order.verify(gateway).findUserById(userId);
        order.verify(generator).nextId();
        order.verify(repository).save(any());

        verifyNoMoreInteractions(repository, generator, gateway);
    }

    @Test
    void shouldThrowUniqueConstraintViolationExceptionTheCreate() {
        Throwable specificCause = mock(Throwable.class);
        when(specificCause.getMessage()).thenReturn("uk_story_user_reaction");

        DataIntegrityViolationException exception = mock(DataIntegrityViolationException.class);
        when(exception.getMostSpecificCause()).thenReturn(specificCause);

        when(gateway.findReactionById(reactionId)).thenReturn(reaction);
        when(gateway.findStoryById(storyId)).thenReturn(story);
        when(gateway.findUserById(userId)).thenReturn(user);
        when(generator.nextId()).thenReturn(storyReaction.getId());
        when(repository.save(any())).thenThrow(exception);

        assertThatThrownBy(() -> this.service.create(userId, storyId, reactionId))
                .isInstanceOf(UniqueConstraintViolationException.class)
                .hasMessage("This story already has this type reacted.");

        InOrder order = inOrder(repository, generator, gateway);

        order.verify(gateway).findReactionById(reactionId);
        order.verify(gateway).findStoryById(storyId);
        order.verify(gateway).findUserById(userId);
        order.verify(generator).nextId();
        order.verify(repository).save(any());

        verifyNoMoreInteractions(repository, generator, gateway);
    }

    @Test
    @DisplayName("Should remove reaction when user reacts with the same reaction type (Toggle Off)")
    void shouldRemoveReactionWhenSameReactionExists() {
        // GIVEN
        when(repository.findByUserIdAndStoryIdAndReactionId(userId, storyId, reactionId))
                .thenReturn(Optional.of(storyReaction));
        doNothing().when(repository).delete(storyReaction);

        // WHEN
        ResultToggle<StoryReactionModel> result = service.react(userId, storyId, reactionId);

        // THEN
        assertThat(result).isNotNull();

        InOrder order = inOrder(repository);
        order.verify(repository).findByUserIdAndStoryIdAndReactionId(userId, storyId, reactionId);
        order.verify(repository).delete(storyReaction);

        verifyNoMoreInteractions(repository, gateway, generator);
    }

    @Test
    @DisplayName("Should create new reaction when no reaction exists for user and story")
    void shouldCreateNewReactionWhenNoneExists() {
        // GIVEN
        when(repository.findByUserIdAndStoryIdAndReactionId(userId, storyId, reactionId))
                .thenReturn(Optional.empty());

        // Mocks necessários para o método this.create() que é chamado internamente
        when(gateway.findReactionById(reactionId)).thenReturn(reaction);
        when(gateway.findStoryById(storyId)).thenReturn(story);
        when(gateway.findUserById(userId)).thenReturn(user);
        when(generator.nextId()).thenReturn(storyReaction.getId());
        when(repository.save(any(StoryReactionModel.class))).thenReturn(storyReaction);

        // WHEN
        ResultToggle<StoryReactionModel> result = service.react(userId, storyId, reactionId);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.body().isPresent()).isNotNull().isTrue();
        assertThat(result.body().get()).isNotNull().isEqualTo(storyReaction);

        // InOrder contemplando o fluxo do react + create
        InOrder order = inOrder(repository, gateway, generator);
        order.verify(repository).findByUserIdAndStoryIdAndReactionId(userId, storyId, reactionId);
        order.verify(gateway).findReactionById(reactionId);
        order.verify(gateway).findStoryById(storyId);
        order.verify(gateway).findUserById(userId);
        order.verify(generator).nextId();
        order.verify(repository).save(any(StoryReactionModel.class));

        verifyNoMoreInteractions(repository, gateway, generator);
    }

}
