package com.blog.writeapi.unit;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.dtos.commentAttachment.CreateCommentAttachmentDTO;
import com.blog.writeapi.models.CommentAttachmentModel;
import com.blog.writeapi.models.CommentModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.repositories.CommentAttachmentRepository;
import com.blog.writeapi.services.providers.CommentAttachmentService;
import com.blog.writeapi.services.providers.StorageService;
import com.blog.writeapi.utils.mappers.CommentAttachmentMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentAttachmentServiceTest {

    @Mock private Snowflake generator;
    @Mock private CommentAttachmentRepository repository;
    @Mock private CommentAttachmentMapper mapper;

    @InjectMocks
    private CommentAttachmentService service;

    @Mock
    private StorageService storageService;

    UserModel user = new UserModel().toBuilder()
            .id(1998780200011111111L)
            .name("user")
            .email("user@gmail.com")
            .password("12345678")
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    CommentModel comment = new CommentModel().toBuilder()
            .id(1998780211111116609L)
            .author(user)
            .build();

    CommentAttachmentModel attachment = new CommentAttachmentModel().toBuilder()
            .id(1998780200074176609L)
            .uploader(user)
            .comment(comment)
            .contentType("image/jpeg")
            .fileName("pochita")
            .fileSize(564765367454L)
            .storageKey(UUID.randomUUID().toString())
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    @Test
    void shouldReturnCommentAttachmentWhenExecFindByIdSimple() {
        when(repository.findById(this.attachment.getId())).thenReturn(Optional.of(this.attachment));

        CommentAttachmentModel model = this.service.findByIdSimple(this.attachment.getId());

        assertThat(model.getId()).isEqualTo(this.attachment.getId());

        verify(repository, times(1)).findById(this.attachment.getId());
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(generator);
    }

    @Test
    void shouldDeleteAttachmentReturnTrue() {
        when(storageService.deleteObject(any(), eq(this.attachment.getStorageKey()), any()))
                .thenReturn(true);

        doNothing().when(repository).delete(attachment);

        boolean deleted = service.delete(this.attachment);

        assertThat(deleted).isTrue();

        verify(storageService).deleteObject(any(), eq(this.attachment.getStorageKey()), any());

        InOrder inOrder = inOrder(this.repository, this.storageService);

        inOrder.verify(this.storageService).deleteObject(any(), eq(this.attachment.getStorageKey()), any());
        inOrder.verify(this.repository).delete(this.attachment);
    }

    @Test
    void shouldDeleteAttachmentReturnFalse() {
        when(storageService.deleteObject(any(), eq(this.attachment.getStorageKey()), any()))
                .thenReturn(false);

        boolean deleted = service.delete(this.attachment);

        assertThat(deleted).isFalse();

        verify(storageService, times(1))
                .deleteObject(any(), eq(this.attachment.getStorageKey()), any());

        verifyNoInteractions(repository);
    }

    @Test
    void shouldCreateNewAttachment() {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getSize()).thenReturn(1024L);

        CreateCommentAttachmentDTO dto = new CreateCommentAttachmentDTO();
        dto.setIsPublic(true);
        dto.setFile(mockFile);

        when(mapper.toModel(any(CreateCommentAttachmentDTO.class)))
                .thenReturn(this.attachment);

        when(generator.nextId()).thenReturn(this.attachment.getId());

        when(storageService.putObject(
                any(),
                anyString(),
                any(),
                eq(mockFile),
                eq(user)
        )).thenReturn(true);

        when(repository.save(any())).thenReturn(this.attachment);

        Optional<CommentAttachmentModel> optional = service.create(dto, user, comment);

        assertThat(optional.isPresent()).isTrue();

        verify(storageService).putObject(any(), anyString(), any(), eq(mockFile), eq(user));
        verify(mapper).toModel(any(CreateCommentAttachmentDTO.class));
        verify(generator).nextId();
        verify(repository).save(attachment);

        InOrder order = inOrder(storageService, mapper, repository, generator);

        order.verify(mapper).toModel(any(CreateCommentAttachmentDTO.class));
        order.verify(storageService).putObject(any(), anyString(), any(), eq(mockFile), eq(user));
        order.verify(generator).nextId();
        order.verify(repository).save(any());
    }

}
