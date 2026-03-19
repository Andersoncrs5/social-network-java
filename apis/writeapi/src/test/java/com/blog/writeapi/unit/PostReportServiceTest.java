package com.blog.writeapi.unit;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.post.models.PostModel;
import com.blog.writeapi.modules.reportPost.dto.CreatePostReportDTO;
import com.blog.writeapi.modules.reportPost.model.PostReportModel;
import com.blog.writeapi.modules.reportPost.repository.PostReportRepository;
import com.blog.writeapi.modules.reportPost.services.provider.PostReportService;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.utils.enums.Post.PostStatusEnum;
import com.blog.writeapi.utils.enums.report.ReportReason;
import com.blog.writeapi.utils.mappers.PostReportMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostReportServiceTest {

    @Mock private PostReportRepository repository;
    @Mock private Snowflake generator;
    @Mock private ObjectMapper objectMapper;
    @Mock private PostReportMapper mapper;

    @InjectMocks private PostReportService service;

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

    @Test
    void shouldReturnTrueWhenExistsByPostAndUser() {
        when(repository.existsByPostAndUser(post, user))
                .thenReturn(true);
        Boolean exists = this.service.existsByPostAndUser(post, user);

        assertThat(exists).isTrue();

        verify(repository, times(1)).existsByPostAndUser(post, user);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldDeletePostReport() {
        doNothing().when(repository).delete(report);

        this.service.delete(report, user.getId());

        verify(repository, times(1)).delete(report);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldCreateNewReport() throws JsonProcessingException {
        CreatePostReportDTO dto = new CreatePostReportDTO(
                "desc",
                ReportReason.HATE_SPEECH,
                post.getId()
        );

        when(mapper.toModel(any(CreatePostReportDTO.class)))
                .thenReturn(this.report);
        when(generator.nextId())
                .thenReturn(this.report.getId());
        when(objectMapper.writeValueAsString(post))
                .thenReturn(this.report.getPostContentSnapshot());
        when(repository.save(any(PostReportModel.class)))
                .thenReturn(this.report);

        PostReportModel model = this.service.create(dto, post, user);

        assertThat(model.getId()).isEqualTo(report.getId());

        verify(mapper, times(1)).toModel(any(CreatePostReportDTO.class));
        verify(generator, times(1)).nextId();
        verify(objectMapper, times(1)).writeValueAsString(post);
        verify(repository, times(1)).save(any(PostReportModel.class));

        InOrder order = inOrder(mapper, generator, objectMapper, repository);

        order.verify(mapper).toModel(any(CreatePostReportDTO.class));
        order.verify(generator).nextId();
        order.verify(objectMapper).writeValueAsString(post);
        order.verify(repository).save(any(PostReportModel.class));

    }

}
