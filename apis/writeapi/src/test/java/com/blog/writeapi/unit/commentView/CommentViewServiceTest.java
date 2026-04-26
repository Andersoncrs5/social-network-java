package com.blog.writeapi.unit.commentView;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.configs.api.metadata.ClientMetadataDTO;
import com.blog.writeapi.modules.comment.models.CommentModel;
import com.blog.writeapi.modules.commentView.gateway.CommentViewModuleGateway;
import com.blog.writeapi.modules.commentView.model.CommentViewModel;
import com.blog.writeapi.modules.commentView.repository.CommentViewRepository;
import com.blog.writeapi.modules.commentView.service.provider.CommentViewService;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.enums.Post.PostStatusEnum;
import com.blog.writeapi.utils.enums.comment.CommentStatusEnum;
import com.blog.writeapi.utils.enums.metric.ActionEnum;
import com.blog.writeapi.utils.enums.metric.CommentMetricEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentViewServiceTest {
    @Mock
    private Snowflake generator;
    @Mock
    private CommentViewModuleGateway gateway;
    @Mock
    private CommentViewRepository repository;

    @InjectMocks
    private CommentViewService service;

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

    CommentModel comment = new CommentModel().toBuilder()
            .id(1998780200074276609L)
            .content("content")
            .status(CommentStatusEnum.APPROVED)
            .post(this.post)
            .author(this.user)
            .parent(null)
            .edited(true)
            .pinned(true)
            .ipAddress("ip-45743567346")
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    CommentViewModel view = new CommentViewModel().toBuilder()
            .id(1998780203279999421L)
            .comment(comment)
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
    void shouldDelete() {
        doNothing()
                .when(repository)
                .delete(view);

        this.service.delete(view);

        verify(repository, times(1)).delete(view);
        verify(gateway, times(1)).handleMetricComment(argThat(i ->
                i.commentId().equals(comment.getId()) &&
                        i.metric().equals(CommentMetricEnum.VIEW) &&
                        i.action().equals(ActionEnum.RED)
        ));
        verifyNoMoreInteractions(repository, gateway);
    }

    @Test
    void shouldReturnTrueWhenExistsByUserAndCommentAndViewedAtDate() {
        when(gateway.getCommentById(view.getComment().getId()))
                .thenReturn(comment);
        when(gateway.getUserById(view.getUser().getId()))
                .thenReturn(user);
        when(repository.existsByUserAndCommentAndViewedAtDate(user, comment, view.getViewedAtDate()))
                .thenReturn(true);

        boolean exists = this.service.existsByUserAndCommentAndViewedAtDate(user.getId(), comment.getId(), view.getViewedAtDate());

        assertThat(exists).isTrue();

        verify(repository, times(1))
                .existsByUserAndCommentAndViewedAtDate(user, comment, view.getViewedAtDate());
        verify(gateway, times(1))
                .getCommentById(comment.getId());
        verify(gateway, times(1))
                .getUserById(user.getId());

        verifyNoMoreInteractions(gateway);
        verifyNoMoreInteractions(repository);

        InOrder order = inOrder(gateway, repository);

        order.verify(gateway).getCommentById(comment.getId());
        order.verify(gateway).getUserById(user.getId());
        order.verify(repository).existsByUserAndCommentAndViewedAtDate(user, comment, view.getViewedAtDate());
    }

    @Test
    void shouldReturnFalseWhenExistsByUserAndCommentAndViewedAtDate() {
        when(gateway.getCommentById(view.getComment().getId()))
                .thenReturn(comment);
        when(gateway.getUserById(view.getUser().getId()))
                .thenReturn(user);
        when(repository.existsByUserAndCommentAndViewedAtDate(user, comment, view.getViewedAtDate()))
                .thenReturn(false);

        boolean exists = this.service.existsByUserAndCommentAndViewedAtDate(user.getId(), comment.getId(), view.getViewedAtDate());

        assertThat(exists).isFalse();

        verify(repository, times(1))
                .existsByUserAndCommentAndViewedAtDate(user, comment, view.getViewedAtDate());
        verify(gateway, times(1))
                .getCommentById(comment.getId());
        verify(gateway, times(1))
                .getUserById(user.getId());

        verifyNoMoreInteractions(gateway);
        verifyNoMoreInteractions(repository);

        InOrder order = inOrder(gateway, repository);

        order.verify(gateway).getCommentById(comment.getId());
        order.verify(gateway).getUserById(user.getId());
        order.verify(repository).existsByUserAndCommentAndViewedAtDate(user, comment, view.getViewedAtDate());
    }

    @Test
    void shouldCreateCommentViewed() {
        when(gateway.getCommentById(view.getComment().getId()))
                .thenReturn(comment);
        when(gateway.getUserById(view.getUser().getId()))
                .thenReturn(user);
        when(generator.nextId())
                .thenReturn(view.getId());
        when(repository.save(any()))
                .thenReturn(view);

        CommentViewModel model = this.service.create(
                view.getUser().getId(),
                view.getComment().getId(),
                metadataDTO,
                view.getViewedAtDate()
        );

        assertThat(model.getId()).isEqualTo(view.getId());

        verify(repository, times(1))
                .save(any());
        verify(generator, times(1))
                .nextId();
        verify(gateway, times(1))
                .getCommentById(comment.getId());
        verify(gateway, times(1))
                .getUserById(user.getId());
        verify(gateway, times(1)).handleMetricComment(argThat(i ->
                i.commentId().equals(comment.getId()) &&
                        i.metric().equals(CommentMetricEnum.VIEW) &&
                        i.action().equals(ActionEnum.SUM)
        ));

        verifyNoMoreInteractions(gateway, repository, generator);

        InOrder order = inOrder(gateway, repository, generator);

        order.verify(gateway).getCommentById(comment.getId());
        order.verify(gateway).getUserById(user.getId());
        order.verify(generator).nextId();
        order.verify(repository).save(any());
        order.verify(gateway).handleMetricComment(any());
    }

}
