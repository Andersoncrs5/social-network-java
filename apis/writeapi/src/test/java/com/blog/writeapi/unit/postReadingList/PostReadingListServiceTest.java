package com.blog.writeapi.unit.postReadingList;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.postReadingList.gateway.PostReadingListModuleGateway;
import com.blog.writeapi.modules.postReadingList.model.PostReadingListModel;
import com.blog.writeapi.modules.postReadingList.repository.PostReadingListRepository;
import com.blog.writeapi.modules.postReadingList.service.provider.PostReadingListService;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.enums.Post.PostStatusEnum;
import com.blog.writeapi.utils.exceptions.BusinessRuleException;
import com.blog.writeapi.utils.exceptions.InternalServerErrorException;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.exceptions.UniqueConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostReadingListServiceTest {

    @Mock private PostReadingListRepository repository;
    @Mock private PostReadingListModuleGateway gateway;
    @Mock private Snowflake generator;

    @InjectMocks private PostReadingListService service;

    UserModel user = UserModel.builder()
            .id(1998780200074176609L)
            .name("user")
            .email("user@gmail.com")
            .password("12345678")
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
            .parent(null)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    PostReadingListModel read = new PostReadingListModel().toBuilder()
            .id(1998780201111111111L)
            .user(user)
            .post(post)
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    @Test
    void shouldDelete() {
        doNothing().when(repository).delete(read);

        this.service.delete(read);

        verify(repository, times(1)).delete(read);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Should return PostReadingListModel when user and post exist")
    void shouldReturnPostReadingListModel_WhenFoundWhenExecMethodFindByUserIdAndPostIdSimple() {
        Long userId = user.getId();
        Long postId = post.getId();

        when(repository.findByUserIdAndPostId(userId, postId))
                .thenReturn(java.util.Optional.of(read));

        PostReadingListModel result = service.findByUserIdAndPostIdSimple(userId, postId);

        assertNotNull(result);
        assertEquals(read.getId(), result.getId());
        assertEquals(user.getId(), result.getUser().getId());
        assertEquals(post.getId(), result.getPost().getId());

        verify(repository, times(1)).findByUserIdAndPostId(userId, postId);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Should throw ModelNotFoundException when record does not exist")
    void shouldThrowException_WhenNotFoundWhenExecMethodFindByUserIdAndPostIdSimple() {
        Long userId = user.getId();
        Long postId = post.getId();

        when(repository.findByUserIdAndPostId(userId, postId))
                .thenReturn(java.util.Optional.empty());

        assertThrows(ModelNotFoundException.class, () -> {
            service.findByUserIdAndPostIdSimple(userId, postId);
        }, "Post not found");

        verify(repository, times(1)).findByUserIdAndPostId(userId, postId);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Should return PostReadingListModel when user and post exist")
    void shouldReturnPostReadingListModel_WhenFound() {
        Long userId = user.getId();
        Long postId = post.getId();

        when(repository.findByUserIdAndPostId(userId, postId))
                .thenReturn(java.util.Optional.of(read));

        var result = service.findByUserIdAndPostId(userId, postId);

        assertTrue(result.isPresent());
        assertEquals(read.getId(), result.get().getId());
        assertEquals(user.getId(), result.get().getUser().getId());
        assertEquals(post.getId(), result.get().getPost().getId());

        verify(repository, times(1)).findByUserIdAndPostId(userId, postId);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Should create a PostReadingListModel successfully")
    void shouldCreatePostReadingListModel() {

        Long userId = user.getId();
        Long postId = post.getId();
        Long generatedId = read.getId();


        when(gateway.findPostById(postId)).thenReturn(post);
        when(gateway.findUserById(userId)).thenReturn(user);
        when(generator.nextId()).thenReturn(generatedId);
        when(repository.save(any(PostReadingListModel.class))).thenReturn(read);


        PostReadingListModel result = service.create(userId, postId);


        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(generatedId);
        assertThat(result.getUser().getName()).isEqualTo("user");
        assertThat(result.getPost().getTitle()).isEqualTo("anyTittle");


        InOrder inOrder = inOrder(gateway, generator, repository);
        inOrder.verify(gateway).findPostById(postId);
        inOrder.verify(gateway).findUserById(userId);
        inOrder.verify(generator).nextId();
        inOrder.verify(repository).save(any(PostReadingListModel.class));

        verifyNoMoreInteractions(gateway, generator, repository);
    }

    @Test
    @DisplayName("Should throw UniqueConstraintViolationException when post is already in user reading list")
    void shouldThrowUniqueConstraintViolationException_WhenRecordAlreadyExists() {

        Long userId = user.getId();
        Long postId = post.getId();

        when(gateway.findPostById(postId)).thenReturn(post);
        when(gateway.findUserById(userId)).thenReturn(user);
        when(generator.nextId()).thenReturn(1L);


        String dbMessage = "Duplicate entry for key 'uk_post_user_reading_list'";
        DataIntegrityViolationException exception = new DataIntegrityViolationException(
                "Conflict",
                new RuntimeException(dbMessage)
        );

        when(repository.save(any(PostReadingListModel.class))).thenThrow(exception);


        assertThatThrownBy(() -> service.create(userId, postId))
                .isInstanceOf(UniqueConstraintViolationException.class)
                .hasMessage("This post already has this type assigned.");

        verify(repository).save(any());
    }

    @Test
    @DisplayName("Should throw InternalServerErrorException when a generic exception occurs")
    void shouldThrowInternalServerErrorException_WhenGenericErrorOccurs() {

        when(gateway.findPostById(anyLong())).thenReturn(post);
        when(gateway.findUserById(anyLong())).thenReturn(user);
        when(generator.nextId()).thenReturn(1L);


        when(repository.save(any())).thenThrow(new RuntimeException("Unexpected error"));


        assertThatThrownBy(() -> service.create(user.getId(), post.getId()))
                .isInstanceOf(InternalServerErrorException.class)
                .hasMessage("Error creating report association.");

        verify(repository).save(any());
    }

    @Test
    @DisplayName("Should throw BusinessRuleException when a generic DataIntegrityViolation occurs")
    void shouldThrowBusinessRuleException_WhenDataIntegrityViolationOccursWithoutSpecificConstraint() {

        when(gateway.findPostById(anyLong())).thenReturn(post);
        when(gateway.findUserById(anyLong())).thenReturn(user);
        when(generator.nextId()).thenReturn(1L);


        String genericDbMessage = "Foreign key violation";
        DataIntegrityViolationException exception = new DataIntegrityViolationException(
                "Integrity Error",
                new RuntimeException(genericDbMessage)
        );

        when(repository.save(any())).thenThrow(exception);


        assertThatThrownBy(() -> service.create(user.getId(), post.getId()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Database integrity error: " + genericDbMessage);
    }

}
