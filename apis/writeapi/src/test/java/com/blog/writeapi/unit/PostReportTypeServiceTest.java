package com.blog.writeapi.unit;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.postReportType.model.PostReportTypeModel;
import com.blog.writeapi.modules.postReportType.repository.PostReportTypeRepository;
import com.blog.writeapi.modules.postReportType.service.provider.PostReportTypeService;
import com.blog.writeapi.modules.reportPost.model.PostReportModel;
import com.blog.writeapi.modules.reportType.model.ReportTypeModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.classes.ResultToggle;
import com.blog.writeapi.utils.enums.Post.PostStatusEnum;
import com.blog.writeapi.utils.enums.global.ToggleEnum;
import com.blog.writeapi.utils.enums.report.ReportPriority;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostReportTypeServiceTest {

    @Mock private PostReportTypeRepository repository;
    @Mock private Snowflake generator;
    @InjectMocks private PostReportTypeService service;

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

    PostReportModel report = new PostReportModel().toBuilder()
            .id(1998780211111111109L)
            .description("any desc")
            .post(post)
            .user(user)
            .postAuthorId(2222222222222222222L)
            .postContentSnapshot("postJson")
            .aiToxicityScore(2.1)
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    ReportTypeModel type = new ReportTypeModel().toBuilder()
            .id(1998780200074176609L)
            .name("report")
            .description("Desc")
            .defaultPriority(ReportPriority.LOW)
            .isActive(true)
            .isVisible(true)
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    PostReportTypeModel reportType = new PostReportTypeModel().toBuilder()
            .id(1991111111111111609L)
            .report(this.report)
            .type(type)
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    @Test
    void shouldReturnPostReportTypeGetById() {
        when(repository.findById(reportType.getId())).thenReturn(Optional.of(reportType));

        Optional<PostReportTypeModel> optional = this.service.getById(reportType.getId());

        assertThat(optional.isPresent()).isTrue();
        assertThat(optional.get().getId()).isEqualTo(reportType.getId());

        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnNullGetById() {
        when(repository.findById(eq(reportType.getId())))
                .thenReturn(Optional.empty());

        Optional<PostReportTypeModel> optional = this.service.getById(reportType.getId());

        assertThat(optional.isEmpty()).isTrue();

        verifyNoMoreInteractions(repository);
        verify(repository, times(1)).findById(anyLong());
    }

    @Test
    void shouldDeletePostReportType() {
        doNothing()
                .when(repository)
                .delete(this.reportType);

        this.service.delete(this.reportType);

        verifyNoMoreInteractions(repository);
        verify(repository, times(1))
                .delete(any(PostReportTypeModel.class));
    }

    @Test
    void shouldCreatePostReportTypeInToggle() {
        Long generatedId = reportType.getId();
        when(repository.findByReportAndType(report, type)).thenReturn(Optional.empty());
        when(generator.nextId()).thenReturn(generatedId);
        when(repository.save(any(PostReportTypeModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResultToggle<PostReportTypeModel> toggle = this.service.toggle(report, type);

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
    void shouldDeletePostReportTypeInToggle() {
        when(repository.findByReportAndType(report, type))
                .thenReturn(Optional.of(reportType));
        doNothing()
                .when(repository)
                .delete(reportType);

        ResultToggle<PostReportTypeModel> toggle = this.service.toggle(report, type);

        assertThat(toggle.body().isPresent()).isTrue();
        assertThat(toggle.result()).isEqualTo(ToggleEnum.REMOVED);

        verify(repository, times(1)).delete(reportType);
        verify(repository, times(1)).findByReportAndType(report, type);
        verify(generator, never()).nextId();
        verify(repository, never()).save(any());

        InOrder order = inOrder(repository, generator);

        order.verify(repository).findByReportAndType(report, type);
        order.verify(repository).delete(reportType);
    }

}
