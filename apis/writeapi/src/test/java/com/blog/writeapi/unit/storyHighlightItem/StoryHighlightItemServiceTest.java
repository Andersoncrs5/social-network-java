package com.blog.writeapi.unit.storyHighlightItem;

import com.blog.writeapi.modules.StoryHighlightItem.dto.CreateStoryHighlightItemDTO;
import com.blog.writeapi.modules.StoryHighlightItem.gateway.StoryHighlightItemModuleGateway;
import com.blog.writeapi.modules.StoryHighlightItem.model.StoryHighlightItemModel;
import com.blog.writeapi.modules.StoryHighlightItem.repository.StoryHighlightItemRepository;
import com.blog.writeapi.modules.StoryHighlightItem.service.provider.StoryHighlightItemService;
import com.blog.writeapi.modules.stories.model.StoryModel;
import com.blog.writeapi.modules.storyHighlight.model.StoryHighlightModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.enums.attachment.AttachmentTypeEnum;
import com.blog.writeapi.utils.result.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StoryHighlightItemServiceTest {

    @Mock private StoryHighlightItemRepository repository;
    @Mock private StoryHighlightItemModuleGateway gateway;

    @InjectMocks private StoryHighlightItemService service;

    private UserModel user;
    private StoryHighlightModel highlight;
    private StoryModel story;
    private StoryHighlightItemModel item;
    private CreateStoryHighlightItemDTO dto;

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

        highlight = new StoryHighlightModel().toBuilder()
                .id(1998780111111111111L)
                .title("title")
                .storageKey("76573457375672349")
                .fileName("pochita")
                .contentType("image/png")
                .type(AttachmentTypeEnum.IMAGE)
                .fileSize(4435645L)
                .isPublic(true)
                .isVisible(true)
                .user(user)
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

        item = StoryHighlightItemModel.builder()
                .id(222222222222222222L)
                .highlight(highlight)
                .user(user)
                .story(story)
                .version(1L)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        dto = new CreateStoryHighlightItemDTO(
                highlight.getId(),
                story.getId()
        );

    }

    @Test
    void shouldCreate() {
        when(gateway.findUserById(user.getId())).thenReturn(user);
        when(gateway.findStoryById(story.getId())).thenReturn(story);
        when(gateway.findHighlightById(highlight.getId())).thenReturn(highlight);
        when(repository.existsByStoryIdAndHighlightIdAndUserId(story.getId(), highlight.getId(), user.getId()))
                .thenReturn(false);
        when(repository.save(any())).thenReturn(item);
        doNothing().when(gateway).toggleStoryHighlight(any());

        Result<StoryHighlightItemModel> result = this.service.create(user.getId(), dto);

        assertThat(result.getStatus()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.isSuccess()).isTrue();

        verify(gateway, times(1)).findUserById(user.getId());
        verify(gateway, times(1)).findStoryById(story.getId());
        verify(gateway, times(1)).findHighlightById(highlight.getId());
        verify(repository, times(1)).existsByStoryIdAndHighlightIdAndUserId(story.getId(), highlight.getId(), user.getId());
        verify(repository, times(1)).save(any());
        verify(gateway, atMostOnce()).toggleStoryHighlight(any());
        verifyNoMoreInteractions(repository, gateway);

        InOrder order = inOrder(repository, gateway);
        order.verify(gateway).findHighlightById(highlight.getId());
        order.verify(gateway).findStoryById(story.getId());
        order.verify(gateway).findUserById(user.getId());
        order.verify(repository).existsByStoryIdAndHighlightIdAndUserId(story.getId(), highlight.getId(), user.getId());
        order.verify(repository).save(any());
        order.verify(gateway).toggleStoryHighlight(any());
    }

    @Test
    void shouldReturnResultConflictWhenCreate() {
        when(gateway.findUserById(user.getId())).thenReturn(user);
        when(gateway.findStoryById(story.getId())).thenReturn(story);
        when(gateway.findHighlightById(highlight.getId())).thenReturn(highlight);
        when(repository.existsByStoryIdAndHighlightIdAndUserId(story.getId(), highlight.getId(), user.getId()))
                .thenReturn(true);

        Result<StoryHighlightItemModel> result = this.service.create(user.getId(), dto);

        assertThat(result.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(result.isFailure()).isTrue();

        verify(gateway, times(1)).findUserById(user.getId());
        verify(gateway, times(1)).findStoryById(story.getId());
        verify(gateway, times(1)).findHighlightById(highlight.getId());
        verify(repository, times(1)).existsByStoryIdAndHighlightIdAndUserId(story.getId(), highlight.getId(), user.getId());
        verify(repository, never()).save(any());
        verify(gateway, never()).toggleStoryHighlight(any());

        verifyNoMoreInteractions(repository, gateway);

        InOrder order = inOrder(repository, gateway);
        order.verify(gateway).findHighlightById(highlight.getId());
        order.verify(gateway).findStoryById(story.getId());
        order.verify(gateway).findUserById(user.getId());
        order.verify(repository).existsByStoryIdAndHighlightIdAndUserId(story.getId(), highlight.getId(), user.getId());
    }

    @Test
    void shouldThrowDataIntegrityViolationExceptionInCreate() {
        var cause = new RuntimeException("uk_story_highlight_items_user_story_highlight");
        var exception = new DataIntegrityViolationException("Duplicado", cause);

        when(gateway.findUserById(user.getId())).thenReturn(user);
        when(gateway.findStoryById(story.getId())).thenReturn(story);
        when(gateway.findHighlightById(highlight.getId())).thenReturn(highlight);
        when(repository.existsByStoryIdAndHighlightIdAndUserId(story.getId(), highlight.getId(), user.getId()))
                .thenReturn(false);
        when(repository.save(any())).thenThrow(exception);

        Result<StoryHighlightItemModel> result = this.service.create(user.getId(), dto);

        assertThat(result.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(result.isFailure()).isTrue();

        verify(gateway, times(1)).findUserById(user.getId());
        verify(gateway, times(1)).findStoryById(story.getId());
        verify(gateway, times(1)).findHighlightById(highlight.getId());
        verify(repository, times(1)).existsByStoryIdAndHighlightIdAndUserId(story.getId(), highlight.getId(), user.getId());
        verify(repository, times(1)).save(any());
        verify(gateway, never()).toggleStoryHighlight(any());
        verifyNoMoreInteractions(repository, gateway);

        InOrder order = inOrder(repository, gateway);
        order.verify(gateway).findHighlightById(highlight.getId());
        order.verify(gateway).findStoryById(story.getId());
        order.verify(gateway).findUserById(user.getId());
        order.verify(repository).existsByStoryIdAndHighlightIdAndUserId(story.getId(), highlight.getId(), user.getId());
        order.verify(repository).save(any());

    }

    @Test
    void shouldDeleteStoryHighlight() {
        when(repository.deleteByID(anyLong())).thenReturn(1);

        Result<Void> result = this.service.deleteById(item.getId());

        assertThat(result.isSuccess()).isTrue();

        verify(repository, times(1)).deleteByID(anyLong());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnNotFoundTheDelete() {
        when(repository.deleteByID(anyLong())).thenReturn(0);

        Result<Void> result = this.service.deleteById(item.getId());

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);

        verify(repository, times(1)).deleteByID(anyLong());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnConflictBecauseHighlightIsNotYour() {
        UserModel user1 = new UserModel().toBuilder().id(1998780222122222221L).build();
        StoryHighlightModel highlight1 = new StoryHighlightModel().toBuilder().id(1998780111111111111L).user(user1).build();

        when(gateway.findHighlightById(highlight1.getId())).thenReturn(highlight1);

        Result<StoryHighlightItemModel> result = this.service.create(user.getId(), new CreateStoryHighlightItemDTO(
                highlight1.getId(),
                story.getId()
        ));

        assertThat(result.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError().message()).isNotBlank().containsIgnoringCase("This highlight is not your.");
        verify(gateway, times(0)).findUserById(anyLong());
        verify(gateway, never()).findStoryById(story.getId());
        verify(gateway, times(1)).findHighlightById(highlight1.getId());
        verify(repository, never()).existsByStoryIdAndHighlightIdAndUserId(story.getId(), highlight.getId(), user.getId());
        verify(repository, never()).save(any());
        verify(gateway, never()).toggleStoryHighlight(any());
        verifyNoMoreInteractions(repository, gateway);
    }

    @Test
    void shouldReturnForbBecauseStoryNotIsYour() {
        UserModel user1 = new UserModel().toBuilder().id(1998780222122222221L).build();
        StoryModel story1 = new StoryModel().toBuilder().id(1998222222122222221L)
                .user(user1)
                .build();

        when(gateway.findHighlightById(highlight.getId())).thenReturn(highlight);
        when(gateway.findStoryById(story1.getId())).thenReturn(story1);

        Result<StoryHighlightItemModel> result = this.service.create(user.getId(), new CreateStoryHighlightItemDTO(
                highlight.getId(),
                story1.getId()
        ));
        assertThat(result.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError().message()).isNotBlank().containsIgnoringCase("This story is not your!");

        verify(gateway, times(0)).findUserById(anyLong());
        verify(gateway, times(1)).findStoryById(story1.getId());
        verify(gateway, times(1)).findHighlightById(highlight.getId());
        verify(repository, never()).existsByStoryIdAndHighlightIdAndUserId(story.getId(), highlight.getId(), user.getId());
        verify(repository, never()).save(any());
        verify(gateway, never()).toggleStoryHighlight(any());
        verifyNoMoreInteractions(repository, gateway);
    }

}
