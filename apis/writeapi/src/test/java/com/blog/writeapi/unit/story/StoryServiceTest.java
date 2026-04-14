package com.blog.writeapi.unit.story;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.stories.dto.CreateStoryDTO;
import com.blog.writeapi.modules.stories.gateway.StoryModuleGateway;
import com.blog.writeapi.modules.stories.model.StoryModel;
import com.blog.writeapi.modules.stories.repository.StoryRepository;
import com.blog.writeapi.modules.stories.service.provider.StoryService;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.enums.attachment.AttachmentTypeEnum;
import com.blog.writeapi.utils.exceptions.BusinessRuleException;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.mappers.StoryMapper;
import com.blog.writeapi.utils.services.providers.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StoryServiceTest {

    @Mock private StoryRepository repository;
    @Mock private StorageService storageService;
    @Mock private Snowflake generator;
    @Mock private StoryMapper mapper;
    @Mock private StoryModuleGateway gateway;

    @InjectMocks
    private StoryService service;

    private CreateStoryDTO dto;
    private UserModel user;
    private StoryModel story;
    private MultipartFile mockFile;

    @BeforeEach
    void setup() {
        dto = new CreateStoryDTO();
        mockFile = mock(MultipartFile.class);

        dto.setIsPublic(true);
        dto.setFile(mockFile);

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
                .build();
    }

    @Test
    @DisplayName("Should create a new story with valid data")
    void shouldCreateNewStory() {
        when(mockFile.getSize()).thenReturn(2343L);
        ObjectCannedACL acl = dto.getIsPublic() ? ObjectCannedACL.PUBLIC_READ : ObjectCannedACL.PRIVATE;

        when(gateway.findUserById(user.getId())).thenReturn(user);
        when(mapper.toModel(dto)).thenReturn(story);
        when(storageService.putObject(any(), anyString(), eq(acl), any(), eq(user))).thenReturn(true);
        when(generator.nextId()).thenReturn(story.getId());
        when(repository.save(any(StoryModel.class))).thenReturn(story);

        // Act
        StoryModel result = service.create(user.getId(), dto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(story.getId());
        assertThat(result.getFileSize()).isEqualTo(2343L);

        // Verify Order
        InOrder order = inOrder(gateway, mapper, storageService, generator, repository);
        order.verify(gateway).findUserById(user.getId());
        order.verify(mapper).toModel(dto);
        order.verify(storageService).putObject(any(), anyString(), eq(acl), any(), eq(user));
        order.verify(generator).nextId();
        order.verify(repository).save(any(StoryModel.class));

        verifyNoMoreInteractions(repository, gateway, generator, mapper, storageService);
    }

    @Test
    @DisplayName("Should fail to create a new story when upload to S3 fails")
    void shouldFailTheCreateNewStory() {
        // Arrange
        ObjectCannedACL acl = dto.getIsPublic() ? ObjectCannedACL.PUBLIC_READ : ObjectCannedACL.PRIVATE;

        when(gateway.findUserById(user.getId())).thenReturn(user);
        when(mapper.toModel(dto)).thenReturn(story);
        when(storageService.putObject(any(), anyString(), eq(acl), any(), eq(user))).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> service.create(user.getId(), dto))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Error the send file to aws s3");

        InOrder order = inOrder(gateway, mapper, storageService);
        order.verify(gateway).findUserById(user.getId());
        order.verify(mapper).toModel(dto);
        order.verify(storageService).putObject(any(), anyString(), eq(acl), any(), eq(user));

        verifyNoMoreInteractions(repository, gateway, generator, mapper, storageService);
    }

    @Test
    @DisplayName("Should delete a story successfully")
    void shouldDelete() {
        when(storageService.deleteObject(any(), eq(story.getStorageKey()), any())).thenReturn(true);
        doNothing().when(repository).delete(story);

        boolean delete = service.delete(story);

        assertThat(delete).isTrue();

        verify(repository, times(1)).delete(story);
        verify(storageService, times(1)).deleteObject(any(), eq(story.getStorageKey()), any());
        verifyNoMoreInteractions(repository, storageService);
    }

    @Test
    @DisplayName("Should fail the delete a story successfully")
    void shouldFailDelete() {
        when(storageService.deleteObject(any(), eq(story.getStorageKey()), any())).thenReturn(false);

        boolean delete = service.delete(story);

        assertThat(delete).isFalse();

        verify(repository, times(0)).delete(any());
        verify(storageService, times(1)).deleteObject(any(), eq(story.getStorageKey()), any());

        verifyNoMoreInteractions(repository, storageService);
    }

    @Test
    @DisplayName("Should return a story when searching by a valid ID")
    void shouldFindById() {
        Long storyId = story.getId();
        when(repository.findById(storyId)).thenReturn(Optional.of(story));

        StoryModel foundStory = service.findById(storyId);

        assertThat(foundStory).isNotNull();
        assertThat(foundStory.getId()).isEqualTo(storyId);
        assertThat(foundStory.getUser().getName()).isEqualTo("user");

        verify(repository, times(1)).findById(storyId);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Should throw ModelNotFoundException when story does not exist")
    void shouldThrowExceptionWhenIdNotFound() {
        Long invalidId = 999L;
        when(repository.findById(invalidId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(invalidId))
                .isInstanceOf(ModelNotFoundException.class)
                .hasMessage("Story not found");

        verify(repository, times(1)).findById(invalidId);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldSuccessArchiveExpiredStories() {
        when(repository.archiveAllExpired(any(OffsetDateTime.class))).thenReturn(10);
        this.service.archiveExpiredStories();
    }

    @Test
    @DisplayName("Should archive expired stories using pagination")
    void shouldArchiveExpiredStoriesWithPagination() {
        StoryModel expired1 = new StoryModel().toBuilder().id(1L).isArchived(false).build();
        StoryModel expired2 = new StoryModel().toBuilder().id(2L).isArchived(false).build();

        Page<StoryModel> firstPage = new PageImpl<>(List.of(expired1), PageRequest.of(0, 100), 2);
        Page<StoryModel> secondPage = new PageImpl<>(List.of(expired2), PageRequest.of(0, 100), 2);

        when(repository.findAllByExpiresAtBeforeAndIsArchivedFalse(any(OffsetDateTime.now().getClass()), any(Pageable.class)))
                .thenReturn(firstPage)
                .thenReturn(secondPage);

        service.archiveExpiredStoriesPageable();
    }

}