package com.blog.writeapi.unit.storyHighlight;

import com.blog.writeapi.modules.storyHighlight.dto.CreateStoryHighlightDTO;
import com.blog.writeapi.modules.storyHighlight.dto.UpdateStoryHighlightDTO;
import com.blog.writeapi.modules.storyHighlight.gateway.StoryHighlightModuleGateway;
import com.blog.writeapi.modules.storyHighlight.model.StoryHighlightModel;
import com.blog.writeapi.modules.storyHighlight.repository.StoryHighlightRepository;
import com.blog.writeapi.modules.storyHighlight.service.provider.StoryHighlightService;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.enums.attachment.AttachmentTypeEnum;
import com.blog.writeapi.utils.exceptions.BusinessRuleException;
import com.blog.writeapi.utils.mappers.StoryHighlightMapper;
import com.blog.writeapi.utils.result.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StoryHighlightServiceTest {

    @Mock private StoryHighlightRepository repository;
    @Mock private StoryHighlightMapper mapper;
    @Mock private StoryHighlightModuleGateway gateway;

    @InjectMocks
    private StoryHighlightService service;

    private UserModel user;
    private StoryHighlightModel highlight;
    private MultipartFile mockFile;
    private CreateStoryHighlightDTO dto;

    @BeforeEach
    void setup() {
        dto = new CreateStoryHighlightDTO();
        mockFile = mock(MultipartFile.class);

        lenient().when(mockFile.getOriginalFilename()).thenReturn("test-image.jpg");
        lenient().when(mockFile.getContentType()).thenReturn("image/jpeg");

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

        dto.setTitle(highlight.getTitle());
        dto.setContentType(highlight.getContentType());
        dto.setIsPublic(true);
        dto.setIsVisible(true);
        dto.setFile(mockFile);
        dto.setFileName(highlight.getFileName());

    }

    @Test
    void shouldDelete() {
        when(gateway.deleteObject(eq(highlight.getStorageKey()), isNull())).thenReturn(true);
        doNothing().when(repository).delete(highlight);

        boolean delete = this.service.delete(highlight);

        assertThat(delete).isTrue();

        verify(gateway, times(1)).deleteObject(eq(highlight.getStorageKey()), isNull());
        verify(repository, times(1)).delete(any());
        verifyNoMoreInteractions(repository, gateway);

        InOrder order = inOrder(repository, gateway);
        order.verify(gateway).deleteObject(eq(highlight.getStorageKey()), isNull());
        order.verify(repository).delete(highlight);
    }

    @Test
    void shouldReturnFalseBecauseObjectNotDeletedDelete() {
        when(gateway.deleteObject(eq(highlight.getStorageKey()), isNull())).thenReturn(false);

        boolean delete = this.service.delete(highlight);

        assertThat(delete).isFalse();

        verify(gateway, times(1)).deleteObject(eq(highlight.getStorageKey()), isNull());
        verify(repository, never()).delete(any());
        verifyNoMoreInteractions(repository, gateway);
    }

    @Test
    void shouldCreate() {
        ObjectCannedACL acl = dto.getIsPublic() ? ObjectCannedACL.PUBLIC_READ :  ObjectCannedACL.PRIVATE;

        when(gateway.findUserById(user.getId())).thenReturn(user);
        when(mapper.toModel(any(CreateStoryHighlightDTO.class))).thenReturn(highlight);
        when(gateway.putObject(anyString(), eq(acl), eq(mockFile), any(UserModel.class))).thenReturn(true);
        when(repository.save(any())).thenReturn(highlight);

        StoryHighlightModel model = this.service.create(user.getId(), dto);
        assertThat(model).isNotNull().isEqualTo(highlight);

        verify(gateway, times(1)).findUserById(user.getId());
        verify(mapper, times(1)).toModel(any(CreateStoryHighlightDTO.class));
        verify(gateway, times(1)).putObject(anyString(), eq(acl), eq(mockFile), any(UserModel.class));
        verify(repository, times(1)).save(any());

        InOrder order = inOrder(gateway, mapper, repository);
        order.verify(gateway).findUserById(user.getId());
        order.verify(mapper).toModel(any(CreateStoryHighlightDTO.class));
        order.verify(gateway).putObject(anyString(), eq(acl), eq(mockFile), any(UserModel.class));
        order.verify(repository).save(any());

        verifyNoMoreInteractions(gateway, mapper, repository);
    }

    @Test
    void shouldThrowBusinessRuleExceptionBecauseObjectCreate() {
        ObjectCannedACL acl = dto.getIsPublic() ? ObjectCannedACL.PUBLIC_READ :  ObjectCannedACL.PRIVATE;

        when(gateway.findUserById(user.getId())).thenReturn(user);
        when(mapper.toModel(any(CreateStoryHighlightDTO.class))).thenReturn(highlight);
        when(gateway.putObject(anyString(), eq(acl), eq(mockFile), any(UserModel.class))).thenReturn(false);

        assertThatThrownBy(() -> this.service.create(user.getId(), dto))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Error the send file to aws s3");

        verify(gateway, times(1)).findUserById(user.getId());
        verify(mapper, times(1)).toModel(any(CreateStoryHighlightDTO.class));
        verify(gateway, times(1)).putObject(anyString(), eq(acl), eq(mockFile), any(UserModel.class));
        verify(repository, never()).save(any());

        InOrder order = inOrder(gateway, mapper, repository);
        order.verify(gateway).findUserById(user.getId());
        order.verify(mapper).toModel(any(CreateStoryHighlightDTO.class));
        order.verify(gateway).putObject(anyString(), eq(acl), eq(mockFile), any(UserModel.class));

        verifyNoMoreInteractions(gateway, mapper, repository);
    }
    @Test
    void shouldUpdateWithNewFile() {
        // Arrange
        UpdateStoryHighlightDTO updateDto = new UpdateStoryHighlightDTO();
        updateDto.setIsPublic(false);
        updateDto.setFile(mockFile);

        ObjectCannedACL expectedAcl = ObjectCannedACL.PRIVATE;

        when(gateway.deleteObject(eq(highlight.getStorageKey()), isNull())).thenReturn(true);
        doNothing().when(mapper).merge(any(), any());
        when(gateway.putObject(anyString(), eq(expectedAcl), eq(mockFile), eq(user))).thenReturn(true);
        when(repository.save(any())).thenReturn(highlight);

        // Act
        Result<StoryHighlightModel> result = service.update(user, highlight, updateDto);

        // Assert
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(result.getValue()).isEqualTo(highlight);

        verify(gateway).deleteObject(eq("76573457375672349"), isNull());
        verify(gateway).putObject(anyString(), eq(expectedAcl), eq(mockFile), eq(user));
        verify(repository).save(any());
    }

    @Test
    void shouldReturnFailureWhenS3UploadFailsDuringUpdate() {
        // Arrange
        UpdateStoryHighlightDTO updateDto = new UpdateStoryHighlightDTO();
        updateDto.setIsPublic(true);
        updateDto.setFile(mockFile);

        ObjectCannedACL expectedAcl = ObjectCannedACL.PUBLIC_READ;

        when(gateway.deleteObject(anyString(), isNull())).thenReturn(true);
        when(gateway.putObject(anyString(), eq(expectedAcl), eq(mockFile), any(UserModel.class))).thenReturn(false);

        // Act
        Result<StoryHighlightModel> result = service.update(user, highlight, updateDto);

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getError().message()).contains("Error uploading file to AWS");

        verify(repository, never()).save(any());
    }

    @Test
    void shouldReturnFailureWhenS3DeleteFailsDuringUpdate() {
        // Arrange
        UpdateStoryHighlightDTO updateDto = new UpdateStoryHighlightDTO();
        when(gateway.deleteObject(anyString(), isNull())).thenReturn(false);

        // Act
        Result<StoryHighlightModel> result = service.update(user, highlight, updateDto);

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getError().code()).isEqualTo("AWS.DeleteError");

        verify(gateway, never()).putObject(any(), any(), any(), any());
        verify(repository, never()).save(any());
    }


}
