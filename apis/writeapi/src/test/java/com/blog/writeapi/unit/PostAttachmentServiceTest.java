package com.blog.writeapi.unit;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.dtos.postAttachment.CreatePostAttachmentDTO;
import com.blog.writeapi.models.PostAttachmentModel;
import com.blog.writeapi.models.PostModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.models.enums.Post.PostStatusEnum;
import com.blog.writeapi.repositories.PostAttachmentRepository;
import com.blog.writeapi.services.providers.PostAttachmentService;
import com.blog.writeapi.services.providers.StorageService;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.mappers.PostAttachmentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostAttachmentServiceTest {

    @Mock
    private PostAttachmentMapper mapper;
    @Mock
    private PostAttachmentRepository repository;
    @Mock
    private Snowflake generator;

    @InjectMocks
    private PostAttachmentService service;

    @Mock
    private StorageService storageService;

    UserModel user = UserModel.builder()
            .id(1998780200074176609L)
            .name("user")
            .email("user@gmail.com")
            .password("12345678")
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    PostAttachmentModel attachment = new PostAttachmentModel().toBuilder()
            .id(1998780200071111111L)
            .uploader(user)
            .contentType("image/jpeg")
            .fileName("pochita")
            .fileSize(564765367454L)
            .storageKey(UUID.randomUUID().toString())
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

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "BUCKET", "test-bucket");
    }

    @Test
    void shouldReturnAttachmentFindByIdSimple() {
        when(repository.findById(this.attachment.getId()))
                .thenReturn(Optional.of(this.attachment));

        PostAttachmentModel model = this.service.getByIdSimple(this.attachment.getId());

        assertThat(model.getId()).isEqualTo(attachment.getId());

        verify(repository, times(1)).findById(this.attachment.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldThrowModelNotFoundExceptionFindByIdSimple() {
        when(repository.findById(this.attachment.getId()))
                .thenReturn(Optional.empty());

        ModelNotFoundException exception = assertThrows(ModelNotFoundException.class,
                () -> this.service.getByIdSimple(this.attachment.getId())
        );

        assertThat(exception.getMessage()).isEqualTo("Attachment not found");

        verify(repository, times(1)).findById(this.attachment.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnFalseDeleteAttachment() {
        when(storageService.deleteObject(eq("test-bucket"), eq(this.attachment.getStorageKey()), any()))
                .thenReturn(true);

        doNothing().when(repository).delete(this.attachment);

        Boolean deleted = this.service.delete(this.attachment);

        assertThat(deleted).isTrue();

        verifyNoMoreInteractions(repository, storageService);

        verify(repository, times(1)).delete(this.attachment);
        verify(storageService, times(1))
                .deleteObject(eq("test-bucket"), eq(this.attachment.getStorageKey()), any());

        InOrder order = inOrder(repository, storageService);

        order.verify(storageService)
                .deleteObject(eq("test-bucket"), eq(this.attachment.getStorageKey()), any());
        order.verify(repository).delete(this.attachment);

    }

    @Test
    void shouldReturnTrueDeleteAttachment() {
        when(storageService.deleteObject(eq("test-bucket"), eq(this.attachment.getStorageKey()), any()))
                .thenReturn(false);

        Boolean deleted = this.service.delete(this.attachment);

        assertThat(deleted).isFalse();

        verifyNoMoreInteractions(storageService);
        verifyNoInteractions(repository);

        verify(repository, times(0)).delete(this.attachment);
        verify(storageService, times(1))
                .deleteObject(eq("test-bucket"), eq(this.attachment.getStorageKey()), any());

    }

    @Test
    void shouldCreateNewAttachment() {
        when(mapper.toModel(any(CreatePostAttachmentDTO.class))).thenReturn(this.attachment);
        when(generator.nextId()).thenReturn(this.attachment.getId());
        when(storageService.putObject(
                eq("test-bucket"),
                anyString(),
                any(),
                any(),
                eq(user)
        )).thenReturn(true);

        when(repository.save(any(PostAttachmentModel.class))).thenReturn(this.attachment);

        CreatePostAttachmentDTO dto = new CreatePostAttachmentDTO();
        dto.setIsPublic(true);

        Optional<PostAttachmentModel> result = this.service.create(dto, user, post);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(this.attachment.getId());

        verify(storageService).putObject(eq("test-bucket"), anyString(), any(), any(), eq(user));
        verify(repository).save(any(PostAttachmentModel.class));

        verifyNoMoreInteractions(mapper, storageService, repository);
    }

    @Test
    void shouldDeleteAllByPost() {
        List<PostAttachmentModel> list = List.of(attachment);

        when(repository.findAllByPost(post)).thenReturn(list);
        doNothing().when(this.storageService).deleteMultiObject(anyString(), anyList());
        doNothing().when(this.repository).deleteAllInBatch(list);

        this.service.deleteAllByPost(post);

        verifyNoMoreInteractions(repository, storageService);

        verify(repository, times(1)).deleteAllInBatch(list);
        verify(storageService, times(1)).deleteMultiObject(anyString(), anyList());

        InOrder order = inOrder(repository, storageService);

        order.verify(storageService).deleteMultiObject(anyString(), anyList());
        order.verify(repository).deleteAllInBatch(list);
    }

}
