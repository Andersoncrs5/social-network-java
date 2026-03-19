package com.blog.writeapi.unit;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.comment.models.CommentModel;
import com.blog.writeapi.modules.commentReport.model.CommentReportModel;
import com.blog.writeapi.modules.commentReportType.model.CommentReportTypeModel;
import com.blog.writeapi.modules.commentReportType.repository.CommentReportTypeRepository;
import com.blog.writeapi.modules.commentReportType.service.provider.CommentReportTypeService;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.reportType.model.ReportTypeModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.classes.ResultToggle;
import com.blog.writeapi.utils.enums.Post.PostStatusEnum;
import com.blog.writeapi.utils.enums.comment.CommentStatusEnum;
import com.blog.writeapi.utils.enums.global.ToggleEnum;
import com.blog.writeapi.utils.enums.report.ReportPriority;
import com.blog.writeapi.utils.mappers.CommentReportMapper;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentReportTypeServiceTest {

    @Mock
    private CommentReportTypeRepository repository;
    @Mock private Snowflake generator;

    @InjectMocks
    private CommentReportTypeService service;
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

    ReportTypeModel type = new ReportTypeModel().toBuilder()
            .id(6998780200074176609L)
            .name("report")
            .description("Desc")
            .defaultPriority(ReportPriority.LOW)
            .isActive(true)
            .isVisible(true)
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    CommentReportTypeModel commentReportType = new CommentReportTypeModel().toBuilder()
            .id(1666666543211111109L)
            .report(report)
            .type(type)
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    @Test
    void shouldReturnCommentReportTypeGetById() {
        when(repository.findById(commentReportType.getId())).thenReturn(Optional.of(commentReportType));

        Optional<CommentReportTypeModel> optional = this.service.getById(commentReportType.getId());

        assertThat(optional.isPresent()).isTrue();
        assertThat(optional.get().getId()).isEqualTo(commentReportType.getId());

        verify(repository, times(1)).findById(commentReportType.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnNullGetById() {
        when(repository.findById(eq(commentReportType.getId())))
                .thenReturn(Optional.empty());

        Optional<CommentReportTypeModel> optional = this.service.getById(commentReportType.getId());

        assertThat(optional.isEmpty()).isTrue();

        verify(repository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldDeleteCommentReportType() {
        doNothing()
                .when(repository)
                .delete(this.commentReportType);

        this.service.delete(this.commentReportType);

        verify(repository, times(1))
                .delete(any(CommentReportTypeModel.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldCreateCommentReportTypeInToggle() {
        Long generatedId = commentReportType.getId();
        when(repository.findByReportAndType(report, type)).thenReturn(Optional.empty());
        when(generator.nextId()).thenReturn(generatedId);
        when(repository.save(any(CommentReportTypeModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResultToggle<CommentReportTypeModel> toggle = this.service.toggle(report, type, user.getId());

        assertThat(toggle.result()).isEqualTo(ToggleEnum.ADDED);
        assertThat(toggle.body()).isPresent();
        assertThat(toggle.body().get().getId()).isEqualTo(generatedId);
        assertThat(toggle.body().get().getReport()).isEqualTo(report);

        InOrder order = inOrder(repository, generator);
        order.verify(repository).findByReportAndType(report, type);
        order.verify(generator).nextId();
        order.verify(repository).save(any());

        verifyNoMoreInteractions(repository, generator);
    }

    @Test
    void shouldDeleteCommentReportTypeInToggle() {
        when(repository.findByReportAndType(report, type))
                .thenReturn(Optional.of(commentReportType));
        doNothing()
                .when(repository)
                .delete(commentReportType);

        ResultToggle<CommentReportTypeModel> toggle = this.service.toggle(report, type, user.getId());

        assertThat(toggle.body().isEmpty()).isTrue();
        assertThat(toggle.result()).isEqualTo(ToggleEnum.REMOVED);

        InOrder order = inOrder(repository, generator);
        order.verify(repository).findByReportAndType(report, type);
        order.verify(repository).delete(commentReportType);

        verify(generator, never()).nextId();
        verify(repository, never()).save(any());
        verifyNoMoreInteractions(repository, generator);
    }

}
