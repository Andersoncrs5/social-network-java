package com.blog.writeapi.integration;

import com.blog.writeapi.configs.HelperTest;
import com.blog.writeapi.configs.TestContainerConfig;
import com.blog.writeapi.modules.comment.dtos.CommentDTO;
import com.blog.writeapi.modules.comment.repository.CommentRepository;
import com.blog.writeapi.modules.commentReport.dto.CommentReportDTO;
import com.blog.writeapi.modules.commentReport.dto.CreateCommentReportDTO;
import com.blog.writeapi.modules.commentReport.dto.UpdateCommentReportDTO;
import com.blog.writeapi.modules.commentReport.repository.CommentReportRepository;
import com.blog.writeapi.modules.post.dtos.PostDTO;
import com.blog.writeapi.modules.post.repository.PostRepository;
import com.blog.writeapi.utils.enums.report.ModerationActionType;
import com.blog.writeapi.utils.enums.report.ReportReason;
import com.blog.writeapi.utils.enums.report.ReportStatus;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.res.ResponseUserTest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestContainerConfig.class)
public class CommentReportControllerTest {
    private final String URL = "/v1/comment-report";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private PostRepository postRepository;
    @Autowired private CommentRepository commentRepository;
    @Autowired private CommentReportRepository commentReportRepository;
    @Autowired private HelperTest helper;

    @BeforeEach
    void setup () {
        commentReportRepository.deleteAll();
        commentRepository.deleteAll();
        postRepository.deleteAll();
    }

    @Test
    void shouldCreateReport() throws Exception {
        var traceId = UUID.randomUUID().toString();
        ResponseUserTest userTest = this.helper.createUser();
        ResponseUserTest userTest2 = this.helper.createUser();

        PostDTO postDTO = this.helper.createPost(userTest);
        CommentDTO commentDTO = this.helper.createComment(
                userTest,
                postDTO,
                null
        );

        CreateCommentReportDTO dto = new CreateCommentReportDTO(
                "Comment Bad",
                ReportReason.VIOLENCE,
                commentDTO.id()
        );

        MvcResult result = mockMvc.perform(post(this.URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Bearer " + userTest2.tokens().token())
                .header("X-Idempotency-Key", traceId)
        ).andExpect(status().isCreated()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<CommentReportDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<CommentReportDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank().isEqualTo(traceId);
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data()).isNotNull();

        assertThat(response.data().id()).isPositive().isNotZero();
        assertThat(response.data().description()).isEqualTo(dto.description());
        assertThat(response.data().reason()).isEqualTo(dto.reason());
        assertThat(response.data().comment().id()).isEqualTo(dto.commentId());
        assertThat(response.data().user().id()).isEqualTo(userTest2.userDTO().id());
        assertThat(response.data().commentAuthorId()).isEqualTo(commentDTO.user().id());
        assertThat(response.data().commentContentSnapshot()).isNotBlank();
    }

    @Test
    void shouldReturnConflictBecauseReportAlready() throws Exception {
        var traceId = UUID.randomUUID().toString();
        ResponseUserTest userTest = this.helper.createUser();
        ResponseUserTest userTest2 = this.helper.createUser();

        PostDTO postDTO = this.helper.createPost(userTest);
        CommentDTO commentDTO = this.helper.createComment(
                userTest,
                postDTO,
                null
        );

        this.helper.addReportToComment(userTest2, commentDTO);

        CreateCommentReportDTO dto = new CreateCommentReportDTO(
                "Comment Bad",
                ReportReason.VIOLENCE,
                commentDTO.id()
        );

        MvcResult result = mockMvc.perform(post(this.URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Bearer " + userTest2.tokens().token())
                .header("X-Idempotency-Key", traceId)
        ).andExpect(status().isConflict()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturnNotFoundBecauseComment() throws Exception {
        var traceId = UUID.randomUUID().toString();
        ResponseUserTest userTest2 = this.helper.createUser();

        CreateCommentReportDTO dto = new CreateCommentReportDTO(
                "Comment Bad",
                ReportReason.VIOLENCE,
                1998780200074176609L
        );

        MvcResult result = mockMvc.perform(post(this.URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Bearer " + userTest2.tokens().token())
                .header("X-Idempotency-Key", traceId)
        ).andExpect(status().isNotFound()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldDeleteReport() throws Exception {
        var traceId = UUID.randomUUID().toString();
        ResponseUserTest userTest = this.helper.createUser();
        ResponseUserTest userTest2 = this.helper.createUser();

        PostDTO postDTO = this.helper.createPost(userTest);
        CommentDTO commentDTO = this.helper.createComment(
                userTest,
                postDTO,
                null
        );

        CommentReportDTO commentReportDTO = this.helper.addReportToComment(userTest2, commentDTO);

        MvcResult result = mockMvc.perform(delete(this.URL + "/" + commentReportDTO.id())
                .header("Authorization", "Bearer " + userTest2.tokens().token())
                .header("X-Idempotency-Key", traceId)
        ).andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.traceId()).isNotBlank().isEqualTo(traceId);
        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturnNotFoundCommentDeleteReport() throws Exception {
        var traceId = UUID.randomUUID().toString();
        ResponseUserTest userTest2 = this.helper.createUser();

        MvcResult result = mockMvc.perform(delete(this.URL + "/" + 1998780200074176609L)
                .header("Authorization", "Bearer " + userTest2.tokens().token())
                .header("X-Idempotency-Key", traceId)
        ).andExpect(status().isNotFound()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.traceId()).isNotBlank();
        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturnForbBecauseAnotherUserTriedDelete() throws Exception {
        var traceId = UUID.randomUUID().toString();
        ResponseUserTest userTest = this.helper.createUser();
        ResponseUserTest userTest2 = this.helper.createUser();
        ResponseUserTest userTest3 = this.helper.createUser();

        PostDTO postDTO = this.helper.createPost(userTest);
        CommentDTO commentDTO = this.helper.createComment(
                userTest,
                postDTO,
                null
        );

        CommentReportDTO commentReportDTO = this.helper.addReportToComment(userTest2, commentDTO);

        MvcResult result = mockMvc.perform(delete(this.URL + "/" + commentReportDTO.id())
                .header("Authorization", "Bearer " + userTest3.tokens().token())
                .header("X-Idempotency-Key", traceId)
        ).andExpect(status().isForbidden()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldUpdateReport() throws Exception {
        var traceId = UUID.randomUUID().toString();

        ResponseUserTest userTest = this.helper.createUser();
        ResponseUserTest userTest2 = this.helper.createUser();

        ResponseUserTest userModerator = this.helper.loginUserInModerator();

        PostDTO postDTO = this.helper.createPost(userTest);
        CommentDTO commentDTO = this.helper.createComment(userTest, postDTO, null);

        CommentReportDTO commentReportDTO = this.helper.addReportToComment(userTest2, commentDTO);

        UpdateCommentReportDTO dto = new UpdateCommentReportDTO(
                ReportStatus.REJECTED,
                ModerationActionType.NONE,
                "Comment Normal"
        );

        MvcResult result = mockMvc.perform(patch(this.URL + "/" + commentReportDTO.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Bearer " + userModerator.tokens().token())
                .header("X-Idempotency-Key", traceId)
        ).andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<CommentReportDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<CommentReportDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.traceId()).isNotBlank().isEqualTo(traceId);
        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data()).isNotNull();
        assertThat(response.data().id()).isEqualTo(commentReportDTO.id());
        assertThat(response.data().status()).isEqualTo(dto.status());
        assertThat(response.data().moderationActionType()).isEqualTo(ModerationActionType.NONE);
        assertThat(response.data().moderatorNotes()).isEqualTo(dto.moderatorNotes());
    }

    @Test
    void shouldReturnNotFoundWhenUpdateReport() throws Exception {
        var traceId = UUID.randomUUID().toString();
        ResponseUserTest userModerator = this.helper.loginUserInModerator();

        UpdateCommentReportDTO dto = new UpdateCommentReportDTO(
                ReportStatus.REJECTED,
                null,
                "Comment Normal"
        );

        MvcResult result = mockMvc.perform(patch(this.URL + "/" + 1998780200074176609L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Bearer " + userModerator.tokens().token())
                .header("X-Idempotency-Key", traceId)
        ).andExpect(status().isNotFound()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<CommentReportDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<CommentReportDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);

        assertThat(response.data()).isNull();
    }
}
