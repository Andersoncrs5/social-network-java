package com.blog.writeapi.integration;

import com.blog.writeapi.configs.HelperTest;
import com.blog.writeapi.configs.TestContainerConfig;
import com.blog.writeapi.modules.post.repository.PostRepository;
import com.blog.writeapi.modules.postReportType.dto.CreatePostReportTypeDTO;
import com.blog.writeapi.modules.postReportType.repository.PostReportTypeRepository;
import com.blog.writeapi.modules.reportPost.dto.PostReportDTO;
import com.blog.writeapi.modules.reportPost.repository.PostReportRepository;
import com.blog.writeapi.modules.reportType.dto.ReportTypeDTO;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestContainerConfig.class)
public class PostReportTypeControllerTest {
    private final String URL = "/v1/post-report-type";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private HelperTest helper;

    @Autowired private PostRepository postRepository;
    @Autowired private PostReportRepository postReportRepository;
    @Autowired private PostReportTypeRepository postReportTypeRepository;

    @BeforeEach
    void setup() {
        postRepository.deleteAll();
        postReportRepository.deleteAll();
        postReportTypeRepository.deleteAll();
    }

    @Test
    void shouldAddedReportTypeToPostReportType() throws Exception {
        ResponseUserTest userTest = this.helper.createUser();
        ResponseUserTest userTest2 = this.helper.createUser();
        ResponseUserTest superAdm = this.helper.loginSuperAdm();

        ReportTypeDTO reportTypeDTO = this.helper.createReportType(superAdm);
        PostReportDTO postReportDTO = this.helper.createPostReportDTO(userTest, userTest2, this.helper.createPost(userTest));

        CreatePostReportTypeDTO dto = new CreatePostReportTypeDTO(
                postReportDTO.id(),
                reportTypeDTO.id()
        );

        MvcResult result = mockMvc.perform(post(this.URL + "/toggle")
                .header("Authorization", "Bearer " + userTest2.tokens().token())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isCreated()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.data()).isNull();
        assertThat(response.message()).isNotBlank();
    }

    @Test
    void shouldRemoveReportTypeToPostReportType() throws Exception {
        ResponseUserTest userTest = this.helper.createUser();
        ResponseUserTest userTest2 = this.helper.createUser();
        ResponseUserTest superAdm = this.helper.loginSuperAdm();

        ReportTypeDTO reportTypeDTO = this.helper.createReportType(superAdm);
        PostReportDTO postReportDTO = this.helper.createPostReportDTO(userTest, userTest2, this.helper.createPost(userTest));

        this.helper.AddedReportTypeToPostReportType(userTest2, reportTypeDTO, postReportDTO);

        CreatePostReportTypeDTO dto = new CreatePostReportTypeDTO(
                postReportDTO.id(),
                reportTypeDTO.id()
        );

        MvcResult result = mockMvc.perform(post(this.URL + "/toggle")
                .header("Authorization", "Bearer " + userTest2.tokens().token())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.data()).isNull();
        assertThat(response.message()).isNotBlank();
    }

    @Test
    void shouldReturnNotFoundBecausePostReportNotFound() throws Exception {
        ResponseUserTest userTest = this.helper.createUser();
        ResponseUserTest userTest2 = this.helper.createUser();
        ResponseUserTest superAdm = this.helper.loginSuperAdm();

        ReportTypeDTO reportTypeDTO = this.helper.createReportType(superAdm);
        PostReportDTO postReportDTO = this.helper.createPostReportDTO(userTest, userTest2, this.helper.createPost(userTest));

        CreatePostReportTypeDTO dto = new CreatePostReportTypeDTO(
                (postReportDTO.id() + 1),
                reportTypeDTO.id()
        );

        MvcResult result = mockMvc.perform(post(this.URL + "/toggle")
                .header("Authorization", "Bearer " + userTest2.tokens().token())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isNotFound()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.data()).isNull();
        assertThat(response.message()).isNotBlank().containsIgnoringCase("Post Report");
    }

    @Test
    void shouldReturnNotFoundBecauseReportTypeNotFound() throws Exception {
        ResponseUserTest userTest = this.helper.createUser();
        ResponseUserTest userTest2 = this.helper.createUser();
        ResponseUserTest superAdm = this.helper.loginSuperAdm();

        ReportTypeDTO reportTypeDTO = this.helper.createReportType(superAdm);
        PostReportDTO postReportDTO = this.helper.createPostReportDTO(userTest, userTest2, this.helper.createPost(userTest));

        CreatePostReportTypeDTO dto = new CreatePostReportTypeDTO(
                postReportDTO.id(),
                (reportTypeDTO.id() + 1)
        );

        MvcResult result = mockMvc.perform(post(this.URL + "/toggle")
                .header("Authorization", "Bearer " + userTest2.tokens().token())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isNotFound()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.data()).isNull();
        assertThat(response.message()).isNotBlank().containsIgnoringCase("Report type");
    }

    @Test
    void shouldReturnForbBecauseAnotherUser() throws Exception {
        ResponseUserTest userTest = this.helper.createUser();
        ResponseUserTest userTest2 = this.helper.createUser();
        ResponseUserTest userTest3 = this.helper.createUser();
        ResponseUserTest superAdm = this.helper.loginSuperAdm();

        ReportTypeDTO reportTypeDTO = this.helper.createReportType(superAdm);
        PostReportDTO postReportDTO = this.helper.createPostReportDTO(userTest, userTest2, this.helper.createPost(userTest));

        CreatePostReportTypeDTO dto = new CreatePostReportTypeDTO(
                postReportDTO.id(),
                reportTypeDTO.id()
        );

        MvcResult result = mockMvc.perform(post(this.URL + "/toggle")
                .header("Authorization", "Bearer " + userTest3.tokens().token())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isForbidden()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.data()).isNull();
        assertThat(response.status()).isFalse();
        assertThat(response.message()).isNotBlank().containsIgnoringCase("This report is not your");
    }

}
