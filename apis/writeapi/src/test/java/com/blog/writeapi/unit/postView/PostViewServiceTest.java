package com.blog.writeapi.unit.postView;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.configs.api.metadata.ClientMetadataDTO;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.postView.model.PostViewModel;
import com.blog.writeapi.modules.postView.repository.PostViewRepository;
import com.blog.writeapi.modules.postView.service.provider.PostViewService;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.enums.Post.PostStatusEnum;
import com.blog.writeapi.utils.exceptions.BusinessRuleException;
import com.blog.writeapi.utils.exceptions.UniqueConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostViewServiceTest {

    @Mock
    private Snowflake generator;

    @Mock
    private PostViewRepository repository;

    @InjectMocks
    private PostViewService service;

    UserModel user = UserModel.builder()
            .id(1998780200074176609L)
            .name("user")
            .email("user@gmail.com")
            .password("12345678")
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

    PostViewModel view = new PostViewModel().toBuilder()
            .id(1998780203279999421L)
            .post(post)
            .user(user)
            .ipAddress("13423534246452")
            .fingerprint("fingerprint")
            .viewedAtDate(LocalDate.now())
            .bot(false)
            .build();

    ClientMetadataDTO metadataDTO = new ClientMetadataDTO(
            view.getIpAddress(),
            view.getUserAgent(),
            view.getFingerprint()
    );

    @Test
    void shouldReturnTrueWhenExistsByUserAndPost() {
        when(repository.existsByUserAndPost(user, post))
                .thenReturn(true);

        boolean exists = this.service.existsByUserAndPost(user, post);

        assertThat(exists).isTrue();

        verify(repository, times(1)).existsByUserAndPost(user, post);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnFalseWhenExistsByUserAndPost() {
        when(repository.existsByUserAndPost(user, post))
                .thenReturn(false);

        boolean exists = this.service.existsByUserAndPost(user, post);

        assertThat(exists).isFalse();

        verify(repository, times(1)).existsByUserAndPost(user, post);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnTrueWhenExistsByUserAndPostAndViewDate() {
        when(repository.existsByUserAndPostAndViewedAtDate(user, post, view.getViewedAtDate()))
                .thenReturn(true);

        boolean exists = this.service.existsByUserAndPostAndViewDate(user, post, view.getViewedAtDate());

        assertThat(exists).isTrue();

        verify(repository, times(1)).existsByUserAndPostAndViewedAtDate(user, post, view.getViewedAtDate());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnFalseWhenExistsByUserAndPostAndViewDate() {
        when(repository.existsByUserAndPostAndViewedAtDate(user, post, view.getViewedAtDate()))
                .thenReturn(false);

        boolean exists = this.service.existsByUserAndPostAndViewDate(user, post, view.getViewedAtDate());

        assertThat(exists).isFalse();

        verify(repository, times(1)).existsByUserAndPostAndViewedAtDate(user, post, view.getViewedAtDate());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldDelete() {
        doNothing()
                .when(repository)
                .delete(view);

        this.service.delete(view);

        verifyNoMoreInteractions(repository);
        verify(repository, times(1)).delete(view);
    }

    @Test
    void shouldCreatePostView() {
        when(generator.nextId())
                .thenReturn(view.getId());
        when(repository.save(any()))
                .thenReturn(view);

        PostViewModel model = this.service.create(user, post, metadataDTO, view.getViewedAtDate());

        assertThat(model.getId()).isEqualTo(view.getId());

        verify(repository, times(1)).save(any());
        verify(generator, times(1)).nextId();

        InOrder order = inOrder(repository, generator);

        order.verify(generator).nextId();
        order.verify(repository).save(any());
    }

    @Test
    void shouldThrowUniqueConstraintViolationExceptionWhenDailyLimitReached() {
        Exception rootCause = new RuntimeException("Duplicate entry for key 'uk_post_view_daily'");

        DataIntegrityViolationException springException =
                new DataIntegrityViolationException("Conflict", rootCause);

        when(repository.save(any(PostViewModel.class)))
                .thenThrow(springException);

        when(generator.nextId()).thenReturn(view.getId());

        assertThatThrownBy(() ->
                this.service.create(user, post, metadataDTO, view.getViewedAtDate())
        )
                .isInstanceOf(UniqueConstraintViolationException.class)
                .hasMessageContaining("User already view this post");

        verify(repository, times(1)).save(any());
    }

    @Test
    void shouldThrowBusinessRuleExceptionOnOtherDataIntegrityErrors() {
        Exception rootCause = new RuntimeException("Generic database error");
        DataIntegrityViolationException springException =
                new DataIntegrityViolationException("Conflict", rootCause);

        when(repository.save(any())).thenThrow(springException);
        when(generator.nextId()).thenReturn(view.getId());

        assertThatThrownBy(() ->
                this.service.create(user, post, metadataDTO, view.getViewedAtDate())
        )
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Database integrity error");
    }

}
