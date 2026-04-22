package com.blog.writeapi.unit.postShare;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.postShare.gateway.PostShareModuleGateway;
import com.blog.writeapi.modules.postShare.model.PostShareModel;
import com.blog.writeapi.modules.postShare.repository.PostShareRepository;
import com.blog.writeapi.modules.postShare.service.provider.PostShareService;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.enums.Post.PostStatusEnum;
import com.blog.writeapi.utils.enums.postShare.SharePlatformEnum;
import com.blog.writeapi.utils.exceptions.BusinessRuleException;
import com.blog.writeapi.utils.exceptions.InternalServerErrorException;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class PostShareServiceTest {

    @Mock
    private PostShareRepository repository;

    @Mock
    private Snowflake generator;

    @Mock
    private PostShareModuleGateway gateway;

    @InjectMocks
    private PostShareService service;

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

    PostShareModel share = new PostShareModel().toBuilder()
            .id(1111111111274176609L)
            .post(post)
            .user(user)
            .platform(SharePlatformEnum.WHATSAPP)
            .shareUrl("https://github.com")
            .build();

    @Test
    @DisplayName("Should throw BusinessRuleException when user is blocked by the post author")
    void shouldThrowBusinessRuleException_WhenUserIsBlocked() {
        UserModel author = UserModel.builder().id(999L).build();
        PostModel postWithDifferentAuthor = post.toBuilder().author(author).build();

        when(gateway.findUserById(user.getId())).thenReturn(user);
        when(gateway.findPostById(post.getId())).thenReturn(postWithDifferentAuthor);

        when(gateway.isBlocked(user.getId(), author.getId())).thenReturn(true);

        assertThatThrownBy(() ->
                service.create(user.getId(), post.getId(), share.getPlatform())
        )
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("You cannot share a post from a blocked user.");

        verify(repository, never()).save(any());
        verify(gateway).isBlocked(user.getId(), author.getId());
    }

    @Test
    @DisplayName("Should create a PostShareModel successfully")
    void shouldCreatePostShareModel() {
        // Arrange
        Long userId = user.getId();
        Long postId = post.getId();
        Long generatedId = share.getId();

        when(gateway.findUserById(userId)).thenReturn(user);
        when(gateway.findPostById(postId)).thenReturn(post);
        when(generator.nextId()).thenReturn(generatedId);
        when(repository.save(any(PostShareModel.class))).thenReturn(share);

        // Act
        PostShareModel result = service.create(userId, postId, share.getPlatform());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(generatedId);
        assertThat(result.getUser().getId()).isEqualTo(userId);
        assertThat(result.getPost().getId()).isEqualTo(postId);
        assertThat(result.getPlatform()).isEqualTo(SharePlatformEnum.WHATSAPP);

        // Verify Order
        InOrder inOrder = inOrder(gateway, generator, repository);
        inOrder.verify(gateway).findUserById(userId);
        inOrder.verify(gateway).findPostById(postId);
        inOrder.verify(generator).nextId();
        inOrder.verify(repository).save(any(PostShareModel.class));

        verifyNoMoreInteractions(gateway, generator, repository);
    }

    @Test
    @DisplayName("Should throw UniqueConstraintViolationException when post share already exists")
    void shouldThrowUniqueConstraintViolationException_WhenRecordAlreadyExists() {
        // Arrange
        when(gateway.findUserById(anyLong())).thenReturn(user);
        when(gateway.findPostById(anyLong())).thenReturn(post);
        when(generator.nextId()).thenReturn(1L);

        String dbMessage = "Duplicate entry for key 'uk_post_shares'";
        DataIntegrityViolationException exception = new DataIntegrityViolationException(
                "Conflict",
                new RuntimeException(dbMessage)
        );

        when(repository.save(any(PostShareModel.class))).thenThrow(exception);

        // Act & Assert
        assertThatThrownBy(() -> service.create(user.getId(), post.getId(), share.getPlatform()))
                .isInstanceOf(UniqueConstraintViolationException.class)
                .hasMessage("This post already has this shared.");

        verify(repository).save(any());
    }

    @Test
    @DisplayName("Should throw InternalServerErrorException when a generic exception occurs during creation")
    void shouldThrowInternalServerErrorException_WhenGenericErrorOccurs() {
        // Arrange
        when(gateway.findUserById(anyLong())).thenReturn(user);
        when(gateway.findPostById(anyLong())).thenReturn(post);
        when(generator.nextId()).thenReturn(1L);

        when(repository.save(any())).thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        assertThatThrownBy(() -> service.create(user.getId(), post.getId(), share.getPlatform()))
                .isInstanceOf(InternalServerErrorException.class)
                .hasMessage("Error creating share to post.");

        verify(repository).save(any());
    }

    @Test
    @DisplayName("Should throw BusinessRuleException when a generic DataIntegrityViolation occurs")
    void shouldThrowBusinessRuleException_WhenDataIntegrityViolationOccursWithoutSpecificConstraint() {
        // Arrange
        when(gateway.findUserById(anyLong())).thenReturn(user);
        when(gateway.findPostById(anyLong())).thenReturn(post);
        when(generator.nextId()).thenReturn(1L);

        String genericDbMessage = "Foreign key violation";
        DataIntegrityViolationException exception = new DataIntegrityViolationException(
                "Integrity Error",
                new RuntimeException(genericDbMessage)
        );

        when(repository.save(any())).thenThrow(exception);

        // Act & Assert
        assertThatThrownBy(() -> service.create(user.getId(), post.getId(), share.getPlatform()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Database integrity error: " + genericDbMessage);
    }

    @Test
    void shouldReturnUserWhenFindByUserIdAndPostId() {
        when(repository.findByUserIdAndPostId(anyLong(), anyLong())).thenReturn(Optional.of(share));

        Optional<PostShareModel> opt = this.service.findByUserIdAndPostId(user.getId(), post.getId());

        assertThat(opt.isPresent()).isTrue();
        assertThat(opt.get().getId()).isEqualTo(share.getId());
    }

    @Test
    void shouldReturnNullWhenFindByUserIdAndPostId() {
        when(repository.findByUserIdAndPostId(anyLong(), anyLong())).thenReturn(Optional.empty());

        Optional<PostShareModel> opt = this.service.findByUserIdAndPostId(user.getId(), post.getId());

        assertThat(opt.isEmpty()).isTrue();
    }

    @Test
    void shouldReturnUserWhenFindByUserIdAndPostIdAndPlatform() {
        when(repository.findByUserIdAndPostIdAndPlatform(anyLong(), anyLong(), any())).thenReturn(Optional.of(share));

        Optional<PostShareModel> opt = this.service.findByUserIdAndPostIdAndPlatform(user.getId(), post.getId(), share.getPlatform());

        assertThat(opt.isPresent()).isTrue();
        assertThat(opt.get().getId()).isEqualTo(share.getId());
    }

    @Test
    void shouldReturnNullWhenFindByUserIdAndPostIdAndPlatform() {
        when(repository.findByUserIdAndPostIdAndPlatform(anyLong(), anyLong(), any())).thenReturn(Optional.empty());

        Optional<PostShareModel> opt = this.service.findByUserIdAndPostIdAndPlatform(user.getId(), post.getId(), share.getPlatform());

        assertThat(opt.isEmpty()).isTrue();
    }

}
