package com.blog.writeapi.integration;

import com.blog.writeapi.HelperTest;
import com.blog.writeapi.modules.post.dtos.PostDTO;
import com.blog.writeapi.modules.post.repository.PostRepository;
import com.blog.writeapi.modules.reportPost.dto.CreatePostReportDTO;
import com.blog.writeapi.modules.reportPost.dto.PostReportDTO;
import com.blog.writeapi.modules.reportPost.dto.UpdatePostReportDTO;
import com.blog.writeapi.modules.reportPost.repository.PostReportRepository;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PostReportControllerTest {
    private final String URL = "/v1/post-report";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private HelperTest helper;

    @Autowired private PostRepository postRepository;
    @Autowired private PostReportRepository repository;

    @BeforeEach
    void setup() {
        postRepository.deleteAll();
        repository.deleteAll();
    }

    @Test
    void shouldCreateNewReportPost() throws Exception {

        ResponseUserTest userTest = this.helper.createUser();
        ResponseUserTest userTest2 = this.helper.createUser();

        PostDTO postDTO = this.helper.createPost(userTest);

        CreatePostReportDTO dto = new CreatePostReportDTO(
                "Post Bad",
                ReportReason.VIOLENCE,
                postDTO.id()
        );

        MvcResult result = mockMvc.perform(post(this.URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Bearer " + userTest2.tokens().token())
        ).andExpect(status().isCreated()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostReportDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostReportDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data()).isNotNull();

        assertThat(response.data().id()).isPositive().isNotZero();
        assertThat(response.data().description()).isEqualTo(dto.description());
        assertThat(response.data().reason()).isEqualTo(dto.reason());
        assertThat(response.data().post().id()).isEqualTo(dto.postId());
        assertThat(response.data().user().id()).isEqualTo(userTest2.userDTO().id());
        assertThat(response.data().postAuthorId()).isEqualTo(postDTO.author().id());
        assertThat(response.data().postContentSnapshot()).isNotBlank();
    }

    @Test
    void shouldReturnNotFoundWhenCreatePost() throws Exception {
        ResponseUserTest userTest = this.helper.createUser();

        CreatePostReportDTO dto = new CreatePostReportDTO(
                "Post Bad",
                ReportReason.VIOLENCE,
                (userTest.userDTO().id() + 1 )
        );

        MvcResult result = mockMvc.perform(post(this.URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Bearer " + userTest.tokens().token())
        ).andExpect(status().isNotFound()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturnConflictWhenCreatePost() throws Exception {
        ResponseUserTest userTest = this.helper.createUser();
        ResponseUserTest userTest2 = this.helper.createUser();

        PostDTO postDTO = this.helper.createPost(userTest);
        this.helper.createPostReportDTO(userTest, userTest2, postDTO);

        CreatePostReportDTO dto = new CreatePostReportDTO(
                "Post Bad",
                ReportReason.VIOLENCE,
                postDTO.id()
        );

        MvcResult result = mockMvc.perform(post(this.URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Bearer " + userTest2.tokens().token())
        ).andExpect(status().isConflict()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldDeleteReport() throws Exception {
        ResponseUserTest userTest = this.helper.createUser();
        ResponseUserTest userTest2 = this.helper.createUser();

        PostDTO postDTO = this.helper.createPost(userTest);
        PostReportDTO postReportDTO = this.helper.createPostReportDTO(userTest, userTest2, postDTO);

        MvcResult result = mockMvc.perform(delete(this.URL + "/" + postReportDTO.id())
                .header("Authorization", "Bearer " + userTest2.tokens().token())
        ).andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturnNotFoundDeleteReport() throws Exception {
        ResponseUserTest userTest = this.helper.createUser();

        MvcResult result = mockMvc.perform(delete(this.URL + "/" + (userTest.userDTO().id() + 1))
                .header("Authorization", "Bearer " + userTest.tokens().token())
        ).andExpect(status().isNotFound()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturnForbDeleteReport() throws Exception {
        ResponseUserTest userTest = this.helper.createUser();
        ResponseUserTest userTest2 = this.helper.createUser();

        PostDTO postDTO = this.helper.createPost(userTest);
        PostReportDTO postReportDTO = this.helper.createPostReportDTO(userTest, userTest2, postDTO);

        MvcResult result = mockMvc.perform(delete(this.URL + "/" + postReportDTO.id())
                .header("Authorization", "Bearer " + userTest.tokens().token())
        ).andExpect(status().isForbidden()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldUpdateReportAllField() throws Exception {
        ResponseUserTest userTest = this.helper.createUser();
        ResponseUserTest userTest2 = this.helper.createUser();

        ResponseUserTest userModerator = this.helper.loginUserInModerator();

        PostDTO postDTO = this.helper.createPost(userTest);
        PostReportDTO postReportDTO = this.helper.createPostReportDTO(userTest, userTest2, postDTO);

        UpdatePostReportDTO dto = new UpdatePostReportDTO(
                ReportStatus.RESOLVED,
                ModerationActionType.SHADOW_BANNED,
                "Post removed and user take shadow ban"
        );

        MvcResult result = mockMvc.perform(patch(this.URL + "/" + postReportDTO.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Bearer " + userModerator.tokens().token())
        ).andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostReportDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostReportDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data()).isNotNull();

        assertThat(response.data().id()).isEqualTo(postReportDTO.id());
        assertThat(response.data().status()).isEqualTo(dto.status());
        assertThat(response.data().moderationActionType()).isEqualTo(dto.moderationActionType());
        assertThat(response.data().moderatorNotes()).isEqualTo(dto.moderatorNotes());
        assertThat(response.data().moderator().id()).isEqualTo(userModerator.userDTO().id());
    }

    @Test
    void shouldReturnNotFoundUpdateReportAllField() throws Exception {
        ResponseUserTest userTest = this.helper.createUser();
        ResponseUserTest userTest2 = this.helper.createUser();

        ResponseUserTest userModerator = this.helper.loginUserInModerator();

        PostDTO postDTO = this.helper.createPost(userTest);
        PostReportDTO postReportDTO = this.helper.createPostReportDTO(userTest, userTest2, postDTO);

        UpdatePostReportDTO dto = new UpdatePostReportDTO(
                ReportStatus.RESOLVED,
                ModerationActionType.SHADOW_BANNED,
                "Post removed and user take shadow ban"
        );

        MvcResult result = mockMvc.perform(patch(this.URL + "/" + (postReportDTO.id() + 1 ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Bearer " + userModerator.tokens().token())
        ).andExpect(status().isNotFound()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);

        assertThat(response.data()).isNull();
    }

}
