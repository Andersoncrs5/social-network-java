package com.blog.writeapi.unit;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.comment.dtos.CreateCommentDTO;
import com.blog.writeapi.modules.comment.gateway.CommentModuleGateway;
import com.blog.writeapi.modules.comment.models.CommentModel;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.enums.Post.PostStatusEnum;
import com.blog.writeapi.utils.enums.comment.CommentStatusEnum;
import com.blog.writeapi.modules.comment.repository.CommentRepository;
import com.blog.writeapi.modules.comment.service.providers.CommentService;
import com.blog.writeapi.utils.enums.metric.ActionEnum;
import com.blog.writeapi.utils.enums.metric.CommentMetricEnum;
import com.blog.writeapi.utils.enums.metric.PostMetricEnum;
import com.blog.writeapi.utils.enums.metric.UserMetricEnum;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.mappers.CommentMapper;
import com.blog.writeapi.utils.result.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock private CommentRepository repository;
    @Mock private Snowflake generator;
    @Mock private CommentModuleGateway gateway;

    @InjectMocks private CommentService service;
    @Mock private CommentMapper mapper;

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

    CommentModel commentParent = new CommentModel().toBuilder()
            .id(1998780211111176609L)
            .content("content")
            .status(CommentStatusEnum.APPROVED)
            .post(this.post)
            .author(this.user)
            .parent(null)
            .edited(true)
            .pinned(true)
            .ipAddress("ip-45743567346")
            .build();

    CommentModel comment = new CommentModel().toBuilder()
            .id(1998780200074176609L)
            .content("content")
            .status(CommentStatusEnum.APPROVED)
            .post(this.post)
            .author(this.user)
            .parent(commentParent)
            .edited(true)
            .pinned(true)
            .ipAddress("ip-45743567346")
            .build();

    // METHOD : getById
    @Test
    void shouldGetCommentByID() {
        when(repository.findById(this.comment.getId())).thenReturn(Optional.of(this.comment));

        Optional<CommentModel> optional = this.service.getById(this.comment.getId());

        assertThat(optional.isPresent()).isTrue();

        verifyNoMoreInteractions(repository);
        verify(repository, times(1)).findById(this.comment.getId());
    }

    @Test
    void shouldReturnNullWhenGetCommentByID() {
        when(repository.findById(this.comment.getId())).thenReturn(Optional.empty());

        Optional<CommentModel> optional = this.service.getById(this.comment.getId());

        assertThat(optional.isEmpty()).isTrue();

        verifyNoMoreInteractions(repository);
        verify(repository, times(1)).findById(this.comment.getId());
    }

    // METHOD: delete
    @Test
    void shouldDeleteCommentAndHandleMetric() {
        doNothing().when(repository).delete(this.comment);

        this.service.delete(this.comment);

        verify(repository).delete(this.comment);
        verify(gateway).handleMetric(argThat(metric ->
                metric.postId().equals(post.getId()) &&
                        metric.metric() == PostMetricEnum.COMMENT &&
                        metric.action() == ActionEnum.RED
        ));
        verify(gateway).handleMetricUser(argThat(metric ->
                metric.userId().equals(user.getId()) &&
                        metric.metric() == UserMetricEnum.COMMENT &&
                        metric.action() == ActionEnum.RED
        ));
    }

    // DELETE: deleteByID
    @Test
    void shouldDeleteByIdCommentAndHandleMetric() {
        when(repository.deleteByID(comment.getId())).thenReturn(1);
        when(repository.findById(comment.getId())).thenReturn(Optional.of(comment));

        Result<Void> result = this.service.deleteByID(comment.getId());

        assertThat(result.isSuccess()).isTrue();

        verify(gateway).handleMetric(argThat(metric ->
                metric.postId().equals(post.getId()) &&
                        metric.metric() == PostMetricEnum.COMMENT &&
                        metric.action() == ActionEnum.RED
        ));

        verify(gateway).handleMetricComment(argThat(metric ->
                metric.commentId().equals(comment.getParent().getId()) &&
                        metric.metric() == CommentMetricEnum.PARENT &&
                        metric.action() == ActionEnum.RED
        ));

        verify(gateway).handleMetricUser(argThat(metric ->
                metric.userId().equals(user.getId()) &&
                        metric.metric() == UserMetricEnum.COMMENT &&
                        metric.action() == ActionEnum.RED
        ));
    }

    @Test
    void shouldFailTheDeleteCommentById() {
        when(repository.deleteByID(anyLong())).thenReturn(0);

        Result<Void> result = this.service.deleteByID(comment.getId());
        assertThat(result.isFailure()).isTrue();

        verify(repository, times(1)).deleteByID(anyLong());
        verifyNoMoreInteractions(repository);
    }

    // METHOD: getByIdSimple
    @Test
    @DisplayName("Should throw exception when comment not found")
    void shouldThrowExceptionWhenCommentNotFound() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByIdSimple(1L))
                .isInstanceOf(ModelNotFoundException.class);
    }

    @Test
    void shouldGetCommentByIdWhenExecMethodGetByIdSimple(){
        when(repository.findById(this.comment.getId())).thenReturn(Optional.of(this.comment));

        CommentModel optional = this.service.getByIdSimple(this.comment.getId());

        verifyNoMoreInteractions(repository);
        verify(repository, times(1)).findById(this.comment.getId());
    }

    // METHOD: create
    @Test
    @DisplayName("Should create a comment successfully")
    void shouldCreateAComment() {
        CreateCommentDTO dto = new CreateCommentDTO(
                this.comment.getContent(),
                this.post.getId(),
                null
        );

        CommentModel mapped = new CommentModel();
        mapped.setContent(dto.content());

        when(mapper.toModel(dto)).thenReturn(mapped);
        when(this.generator.nextId()).thenReturn(this.comment.getId());
        when(repository.save(any(CommentModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CommentModel result = this.service.create(dto, this.post, this.user);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(this.comment.getId());
        assertThat(result.getAuthor()).isEqualTo(this.user);
        assertThat(result.getPost()).isEqualTo(this.post);
        assertThat(result.getContent()).isEqualTo(dto.content());

        verify(mapper, times(1)).toModel(dto);
        verify(generator, times(1)).nextId();
        verify(repository, times(1)).save(any(CommentModel.class));
    }

    @Test
    @DisplayName("Should create a comment reply and handle metric")
    void shouldCreateCommentReplyAndHandleMetric() {
        CreateCommentDTO dto = new CreateCommentDTO("Reply content", post.getId(), comment.getId());

        when(mapper.toModel(dto)).thenReturn(new CommentModel());
        when(generator.nextId()).thenReturn(123L);
        when(repository.save(any(CommentModel.class))).thenAnswer(i -> i.getArgument(0));

        this.service.create(dto, post, user, comment);

        verify(gateway).handleMetric(argThat(metric ->
                metric.metric() == PostMetricEnum.COMMENT &&
                        metric.action() == ActionEnum.SUM
        ));

        verify(gateway).handleMetricComment(argThat(metric ->
                metric.commentId().equals(comment.getId()) &&
                        metric.metric() == CommentMetricEnum.PARENT &&
                        metric.action() == ActionEnum.SUM
        ));

        verify(gateway).handleMetricUser(argThat(metric ->
                metric.userId().equals(user.getId()) &&
                        metric.metric().equals(UserMetricEnum.COMMENT) &&
                        metric.action().equals(ActionEnum.SUM)
        ));
    }
}
