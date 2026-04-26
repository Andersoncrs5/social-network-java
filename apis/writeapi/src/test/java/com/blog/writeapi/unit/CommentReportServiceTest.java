package com.blog.writeapi.unit;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.comment.models.CommentModel;
import com.blog.writeapi.modules.commentReport.dto.CreateCommentReportDTO;
import com.blog.writeapi.modules.commentReport.gateway.CommentReportModuleGateway;
import com.blog.writeapi.modules.commentReport.model.CommentReportModel;
import com.blog.writeapi.modules.commentReport.repository.CommentReportRepository;
import com.blog.writeapi.modules.commentReport.service.provider.CommentReportService;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.enums.Post.PostStatusEnum;
import com.blog.writeapi.utils.enums.comment.CommentStatusEnum;
import com.blog.writeapi.utils.enums.metric.ActionEnum;
import com.blog.writeapi.utils.enums.metric.CommentMetricEnum;
import com.blog.writeapi.utils.enums.report.ReportReason;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.mappers.CommentReportMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentReportServiceTest {

    @Mock private CommentReportRepository repository;
    @Mock private Snowflake generator;
    @Mock private CommentReportModuleGateway gateway;

    @InjectMocks private CommentReportService service;
    @Mock private ObjectMapper objectMapper;
    @Mock CommentReportMapper mapper;

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

    CommentModel comment = new CommentModel().toBuilder()
            .id(1998780200074176609L)
            .content("content")
            .status(CommentStatusEnum.APPROVED)
            .post(this.post)
            .author(this.user)
            .parent(null)
            .edited(true)
            .pinned(true)
            .ipAddress("ip-45743567346")
            .build();

    CommentReportModel report = new CommentReportModel().toBuilder()
            .id(1998780211111111109L)
            .description("any desc")
            .comment(comment)
            .user(user)
            .commentAuthorId(2222222222222222222L)
            .commentContentSnapshot("postJson")
            .aiToxicityScore(2.1)
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    @Test
    void shouldReturnCommentReportWhenFindByIdSimple() {
        when(repository.findById(this.report.getId()))
                .thenReturn(Optional.of(report));

        CommentReportModel model = this.service.findByIdSimple(report.getId());

        assertThat(model.getId()).isEqualTo(report.getId());

        verify(repository, times(1)).findById(report.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnNullWhenFindByIdSimple() {
        when(repository.findById(this.report.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.service.findByIdSimple(report.getId()))
                .isExactlyInstanceOf(ModelNotFoundException.class)
                        .hasMessage("Comment Report not found");

        verify(repository, times(1)).findById(report.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnCommentReportWhenFindByCommentAndUser() {
        when(repository.findByCommentAndUser(eq(comment), eq(user)))
                .thenReturn(Optional.of(report));

        Optional<CommentReportModel> optional = service.findByCommentAndUser(comment, user);

        assertThat(optional.isPresent()).isTrue();

        verify(repository, times(1)).findByCommentAndUser(comment, user);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnNullWhenFindByCommentAndUser() {
        when(repository.findByCommentAndUser(eq(comment), eq(user)))
                .thenReturn(Optional.empty());

        Optional<CommentReportModel> optional = service.findByCommentAndUser(comment, user);

        assertThat(optional.isEmpty()).isTrue();

        verify(repository, times(1)).findByCommentAndUser(comment, user);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldDeleteCommentReport() {
        doNothing()
                .when(repository)
                .delete(report);

        this.service.delete(report);

        verify(repository, times(1)).delete(report);
        verify(gateway, times(1)).handleMetricComment(argThat(i ->
            i.metric().equals(CommentMetricEnum.REPORT) &&
                    i.action().equals(ActionEnum.RED) &&
                            i.commentId().equals(comment.getId())
        ));
        verifyNoMoreInteractions(repository, gateway);
    }

    @Test
    void shouldReturnTrueWhenExistsByCommentAndUser() {
        when(repository.existsByCommentAndUser(comment, user))
                .thenReturn(true);

        Boolean exists = this.service.existsByCommentAndUser(comment, user);

        assertThat(exists).isTrue();

        verify(repository, times(1)).existsByCommentAndUser(comment, user);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnFalseWhenExistsByCommentAndUser() {
        when(repository.existsByCommentAndUser(comment, user))
                .thenReturn(false);

        Boolean exists = this.service
                .existsByCommentAndUser(comment, user);

        assertThat(exists)
                .isFalse();

        verify(repository, times(1))
                .existsByCommentAndUser(comment, user);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldCreateCommentReport() throws JsonProcessingException {
        CreateCommentReportDTO dto = new CreateCommentReportDTO(
                "AnyDesc",
                ReportReason.OTHER,
                comment.getId()
        );

        when(mapper.toModel(eq(dto)))
                .thenReturn(report);
        when(generator.nextId())
                .thenReturn(report.getId());
        when(objectMapper.writeValueAsString(eq(comment)))
                .thenReturn(report.getCommentContentSnapshot());
        when(repository.save(any(CommentReportModel.class)))
                .thenReturn(report);
        doNothing().when(gateway).handleMetricComment(any());

        CommentReportModel model = this.service.create(dto, comment, user);

        assertThat(model.getId()).isEqualTo(report.getId());

        verify(gateway, times(1)).handleMetricComment(argThat(i ->
                i.metric().equals(CommentMetricEnum.REPORT) &&
                        i.action().equals(ActionEnum.SUM) &&
                        i.commentId().equals(comment.getId())
        ));

        verifyNoMoreInteractions(repository, generator, mapper);

        InOrder order = inOrder(repository, generator, mapper, objectMapper, gateway);

        order.verify(mapper).toModel(eq(dto));
        order.verify(generator).nextId();
        order.verify(objectMapper).writeValueAsString(comment);
        order.verify(repository).save(any(CommentReportModel.class));
        order.verify(gateway).handleMetricComment(any());
    }


}
