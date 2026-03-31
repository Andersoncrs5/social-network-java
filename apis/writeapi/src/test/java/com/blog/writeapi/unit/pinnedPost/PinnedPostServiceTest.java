package com.blog.writeapi.unit.pinnedPost;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.pinnedPost.dto.CreatePinnedPostDTO;
import com.blog.writeapi.modules.pinnedPost.dto.UpdatePinnedPostDTO;
import com.blog.writeapi.modules.pinnedPost.gateway.PinnedPostServiceModuleGateway;
import com.blog.writeapi.modules.pinnedPost.model.PinnedPostModel;
import com.blog.writeapi.modules.pinnedPost.repository.PinnedPostRepository;
import com.blog.writeapi.modules.pinnedPost.service.provider.PinnedPostService;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.enums.Post.PostStatusEnum;
import com.blog.writeapi.utils.exceptions.BusinessRuleException;
import com.blog.writeapi.utils.mappers.PinnedPostMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PinnedPostServiceTest {
    @Mock private PinnedPostRepository repository;
    @Mock private PinnedPostServiceModuleGateway gateway;
    @Mock private PinnedPostMapper mapper;
    @Mock private Snowflake generator;

    @InjectMocks private PinnedPostService service;

    UserModel user = UserModel.builder()
            .id(1998780200074176609L)
            .name("user")
            .email("user@gmail.com")
            .password("12345678")
            .build();

    PostModel post = new PostModel().toBuilder()
            .id(1111111111111176609L)
            .title("anyTittle")
            .slug("any-title")
            .content("any Content")
            .status(PostStatusEnum.PUBLISHED)
            .readingTime(5)
            .rankingScore(3.0)
            .isFeatured(false)
            .author(user)
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    PinnedPostModel pinned = new PinnedPostModel().toBuilder()
            .id(2222222111111176609L)
            .user(user)
            .post(post)
            .orderIndex(1)
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    CreatePinnedPostDTO dto = new CreatePinnedPostDTO(
            post.getId(),
            pinned.getOrderIndex()
    );

    UpdatePinnedPostDTO updateDTO = new UpdatePinnedPostDTO(
            dto.orderIndex()
    );

    @Test
    void shouldReturnTrueWhenExistsByUserIdAndPostId() {
        when(repository.existsByUserIdAndPostId(user.getId(), post.getId()))
                .thenReturn(true);

        boolean exists = this.service.existsByUserIdAndPostId(user.getId(), post.getId());

        assertThat(exists).isTrue();

        verify(repository, times(1)).existsByUserIdAndPostId(user.getId(), post.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnFalseWhenExistsByUserIdAndPostId() {
        when(repository.existsByUserIdAndPostId(user.getId(), post.getId()))
                .thenReturn(false);

        boolean exists = this.service.existsByUserIdAndPostId(user.getId(), post.getId());

        assertThat(exists).isFalse();

        verify(repository, times(1)).existsByUserIdAndPostId(user.getId(), post.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnTrueWhenExistsByUserIdAndOrderIndex() {
        when(repository.existsByUserIdAndOrderIndex(user.getId(), this.pinned.getOrderIndex()))
                .thenReturn(true);

        boolean exists = this.service.existsByUserIdAndOrderIndex(user.getId(), this.pinned.getOrderIndex());

        assertThat(exists).isTrue();

        verify(repository, times(1)).existsByUserIdAndOrderIndex(user.getId(), this.pinned.getOrderIndex());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnFalseWhenExistsByUserIdAndOrderIndex() {
        when(repository.existsByUserIdAndOrderIndex(user.getId(), this.pinned.getOrderIndex()))
                .thenReturn(false);

        boolean exists = this.service.existsByUserIdAndOrderIndex(user.getId(), this.pinned.getOrderIndex());

        assertThat(exists).isFalse();

        verify(repository, times(1)).existsByUserIdAndOrderIndex(user.getId(), this.pinned.getOrderIndex());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldDelete() {
        doNothing().when(repository).delete(pinned);

        this.service.delete(pinned);

        verify(repository, times(1)).delete(pinned);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldCreate() {
        when(repository.countByUserId(user.getId()))
                .thenReturn(0);
        when(gateway.findUserById(user.getId()))
                .thenReturn(user);
        when(gateway.findPostById(post.getId()))
                .thenReturn(post);
        when(generator.nextId())
                .thenReturn(pinned.getId());
        when(repository.save(any()))
                .thenReturn(pinned);

        PinnedPostModel model = this.service.create(user.getId(), dto);

        assertThat(model.getId()).isEqualTo(pinned.getId());

        verify(repository, times(1)).countByUserId(user.getId());
        verify(gateway, times(1)).findUserById(user.getId());
        verify(gateway, times(1)).findPostById(post.getId());
        verify(generator, times(1)).nextId();
        verify(repository, times(1)).save(any());

        InOrder order = inOrder(gateway, generator, repository);

        order.verify(repository).countByUserId(user.getId());
        order.verify(gateway).findUserById(anyLong());
        order.verify(gateway).findPostById(anyLong());
        order.verify(generator).nextId();
        order.verify(repository).save(any());
        order.verifyNoMoreInteractions();
    }

    @Test
    void shouldThrowBusinessRuleException() {
        when(repository.countByUserId(user.getId()))
                .thenReturn(10);

        assertThatThrownBy(() ->
            this.service.create(user.getId(), dto)
        )
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("You have reached the maximum limit of 10 pinned posts.");

        verify(repository, times(1)).countByUserId(user.getId());
        verify(gateway, times(0)).findUserById(user.getId());
        verify(gateway, times(0)).findPostById(post.getId());
        verify(generator, times(0)).nextId();
        verify(repository, times(0)).save(any());

        verifyNoMoreInteractions(repository, generator, generator);
    }

    @Test
    void shouldUpdate() {
        doNothing().when(mapper).updateModel(updateDTO, pinned);
        when(repository.save(any())).thenReturn(pinned);

        PinnedPostModel update = this.service.update(pinned, updateDTO);

        assertThat(update.getId()).isEqualTo(pinned.getId());
        assertThat(update.getOrderIndex()).isEqualTo(pinned.getOrderIndex());

        InOrder order = inOrder(repository, mapper);

        order.verify(mapper).updateModel(any(), any());
        order.verify(repository).save(any());
    }

}
