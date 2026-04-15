package com.blog.writeapi.unit.storyView;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.stories.model.StoryModel;
import com.blog.writeapi.modules.storyView.gateway.StoryViewModuleGateway;
import com.blog.writeapi.modules.storyView.model.StoryViewModel;
import com.blog.writeapi.modules.storyView.repository.StoryViewRepository;
import com.blog.writeapi.modules.storyView.service.provider.StoryViewService;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.enums.attachment.AttachmentTypeEnum;
import com.blog.writeapi.utils.exceptions.BusinessRuleException;
import com.blog.writeapi.utils.exceptions.InternalServerErrorException;
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
public class StoryViewServiceTest {

    @Mock
    private StoryViewRepository repository;
    @Mock
    private StoryViewModuleGateway gateway;

    @Mock
    private Snowflake generator;

    @InjectMocks
    private StoryViewService service;

    UserModel user = new UserModel();
    StoryModel story = new StoryModel();
    StoryViewModel storyView = new StoryViewModel();

    @BeforeEach
    void setup() {
        user = UserModel.builder()
                .id(1998780200074176609L)
                .name("user")
                .email("user@gmail.com")
                .password("12345678")
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
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        storyView = new StoryViewModel().toBuilder()
                .id(2222222222222222222L)
                .user(user)
                .story(story)
                .version(1L)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    @Test
    void shouldReturnStoryViewWhenFindById() {
        when(repository.findById(storyView.getId())).thenReturn(Optional.of(storyView));

        StoryViewModel model = this.service.findByIdSimple(storyView.getId());

        assertThat(model).isEqualTo(storyView);

        verify(repository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Should throw ModelNotFoundException when story does not exist")
    void shouldThrowExceptionWhenIdNotFound() {
        Long invalidId = 999L;
        when(repository.findById(invalidId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByIdSimple(invalidId))
                .isInstanceOf(ModelNotFoundException.class)
                .hasMessage("Story view not found");

        verify(repository, times(1)).findById(invalidId);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldDeleteView() {
        when(repository.deleteAndCount(storyView.getId())).thenReturn(1);

        this.service.delete(storyView.getId());

        verify(repository, times(1)).deleteAndCount(storyView.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnModelNotFoundExceptionDeleteView() {
        when(repository.deleteAndCount(storyView.getId())).thenReturn(0);

        assertThatThrownBy(() -> service.delete(storyView.getId()))
                .isInstanceOf(ModelNotFoundException.class)
                .hasMessageContaining("StoryView not found with id: " + storyView.getId());

        verify(repository, times(1)).deleteAndCount(storyView.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Should create a StoryView successfully")
    void shouldCreateStoryViewSuccessfully() {
        Long userId = user.getId();
        Long storyId = story.getId();
        Long generatedId = storyView.getId();

        when(gateway.findUserById(userId)).thenReturn(user);
        when(gateway.findStoryById(storyId)).thenReturn(story);
        when(generator.nextId()).thenReturn(generatedId);
        when(repository.save(any(StoryViewModel.class))).thenReturn(storyView);

        StoryViewModel result = service.createView(userId, storyId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(generatedId);
        assertThat(result.getUser().getId()).isEqualTo(userId);
        assertThat(result.getStory().getId()).isEqualTo(storyId);

        InOrder inOrder = inOrder(gateway, generator, repository);
        inOrder.verify(gateway).findUserById(userId);
        inOrder.verify(gateway).findStoryById(storyId);
        inOrder.verify(generator).nextId();
        inOrder.verify(repository).save(any(StoryViewModel.class));

        verifyNoMoreInteractions(gateway, generator, repository);
    }

    @Test
    @DisplayName("Should throw UniqueConstraintViolationException when story is already viewed by user")
    void shouldThrowUniqueConstraintViolationException_WhenViewAlreadyExists() {
        Long userId = user.getId();
        Long storyId = story.getId();

        when(gateway.findUserById(userId)).thenReturn(user);
        when(gateway.findStoryById(storyId)).thenReturn(story);
        when(generator.nextId()).thenReturn(1L);

        String dbMessage = "Duplicate entry for key 'uk_story_views_user_story'";
        DataIntegrityViolationException exception = new DataIntegrityViolationException(
                "Conflict", new RuntimeException(dbMessage));

        when(repository.save(any(StoryViewModel.class))).thenThrow(exception);

        // Act & Assert
        assertThatThrownBy(() -> service.createView(userId, storyId))
                .isInstanceOf(UniqueConstraintViolationException.class)
                .hasMessage("You already view this story");

        verify(repository).save(any());
    }

    @Test
    @DisplayName("Should throw BusinessRuleException when a generic DataIntegrityViolation occurs")
    void shouldThrowBusinessRuleException_WhenGenericDataIntegrityViolationOccurs() {
        when(gateway.findUserById(anyLong())).thenReturn(user);
        when(gateway.findStoryById(anyLong())).thenReturn(story);
        when(generator.nextId()).thenReturn(1L);

        String genericDbMessage = "Foreign key violation: story_id";
        DataIntegrityViolationException exception = new DataIntegrityViolationException(
                "Integrity Error", new RuntimeException(genericDbMessage));

        when(repository.save(any())).thenThrow(exception);

        assertThatThrownBy(() -> service.createView(user.getId(), story.getId()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Database integrity error: " + genericDbMessage);
    }

    @Test
    @DisplayName("Should throw InternalServerErrorException when a generic exception occurs")
    void shouldThrowInternalServerErrorException_WhenGenericErrorOccurs() {
        when(gateway.findUserById(anyLong())).thenReturn(user);
        when(gateway.findStoryById(anyLong())).thenReturn(story);
        when(generator.nextId()).thenReturn(1L);

        when(repository.save(any())).thenThrow(new RuntimeException("Unexpected Sytem Failure"));

        assertThatThrownBy(() -> service.createView(user.getId(), story.getId()))
                .isInstanceOf(InternalServerErrorException.class)
                .hasMessage("Error creating report association.");

        verify(repository).save(any());
    }

    @Test
    @DisplayName("Should return false when story view already exists")
    void shouldReturnFalse_WhenViewAlreadyExists() {
        Long userId = 1L;
        Long storyId = 2L;
        when(repository.existsByUserIdAndStoryId(userId, storyId)).thenReturn(true);

        boolean result = service.createIfNotExists(userId, storyId);

        assertThat(result).isFalse();

        verify(gateway, never()).findUserById(any());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should return true and call createView when view does not exist")
    void shouldReturnTrueAndCreateView_WhenViewDoesNotExist() {
        Long userId = user.getId();
        Long storyId = story.getId();

        when(repository.existsByUserIdAndStoryId(userId, storyId)).thenReturn(false);

        when(gateway.findUserById(userId)).thenReturn(user);
        when(gateway.findStoryById(storyId)).thenReturn(story);
        when(generator.nextId()).thenReturn(storyView.getId());
        when(repository.save(any(StoryViewModel.class))).thenReturn(storyView);

        boolean result = service.createIfNotExists(userId, storyId);

        assertThat(result).isTrue();

        InOrder inOrder = inOrder(repository, gateway, generator);
        inOrder.verify(repository).existsByUserIdAndStoryId(userId, storyId);
        inOrder.verify(gateway).findUserById(userId);
        inOrder.verify(gateway).findStoryById(storyId);
        inOrder.verify(repository).save(any(StoryViewModel.class));
    }

}
