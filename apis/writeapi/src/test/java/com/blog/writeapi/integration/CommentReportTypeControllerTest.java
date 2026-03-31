package com.blog.writeapi.integration;

import com.blog.writeapi.HelperTest;
import com.blog.writeapi.modules.comment.dtos.CommentDTO;
import com.blog.writeapi.modules.commentReport.dto.CommentReportDTO;
import com.blog.writeapi.modules.commentReport.repository.CommentReportRepository;
import com.blog.writeapi.modules.commentReportType.dto.CreateCommentReportTypeDTO;
import com.blog.writeapi.modules.commentReportType.repository.CommentReportTypeRepository;
import com.blog.writeapi.modules.post.dtos.PostDTO;
import com.blog.writeapi.modules.post.repository.PostRepository;
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
public class CommentReportTypeControllerTest {
    private final String URL = "/v1/comment-report-type";

    @Autowired
    private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private HelperTest helper;

    @Autowired private PostRepository postRepository;
    @Autowired private CommentReportRepository commentReportRepository;
    @Autowired private CommentReportTypeRepository commentReportTypeRepository;

    @BeforeEach
    void setup() {
        commentReportTypeRepository.deleteAll();
        commentReportRepository.deleteAll();
        postRepository.deleteAll();
    }

    @Test
    void shouldCreate() throws Exception {
        ResponseUserTest superAdm = this.helper.loginSuperAdm();
        ReportTypeDTO reportTypeDTO = this.helper.createReportType(superAdm);

        ResponseUserTest userTest = this.helper.createUser();
        ResponseUserTest userTest2 = this.helper.createUser();

        PostDTO postDTO = this.helper.createPost(userTest);
        CommentDTO commentDTO = this.helper.createComment(userTest, postDTO, null);

        CommentReportDTO commentReportDTO = this.helper.addReportToComment(userTest2, commentDTO);

        CreateCommentReportTypeDTO dto = new CreateCommentReportTypeDTO(
                commentReportDTO.id(),
                reportTypeDTO.id()
        );

        MvcResult result = mockMvc.perform(post(this.URL + "/toggle")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Bearer " + userTest2.tokens().token())
        ).andExpect(status().isCreated()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldRemove() throws Exception {
        ResponseUserTest superAdm = this.helper.loginSuperAdm();
        ReportTypeDTO reportTypeDTO = this.helper.createReportType(superAdm);

        ResponseUserTest userTest = this.helper.createUser();
        ResponseUserTest userTest2 = this.helper.createUser();

        PostDTO postDTO = this.helper.createPost(userTest);
        CommentDTO commentDTO = this.helper.createComment(userTest, postDTO, null);

        CommentReportDTO commentReportDTO = this.helper.addReportToComment(userTest2, commentDTO);
        this.helper.addReportTypeToComment(commentReportDTO, userTest2, reportTypeDTO);

        CreateCommentReportTypeDTO dto = new CreateCommentReportTypeDTO(
                commentReportDTO.id(),
                reportTypeDTO.id()
        );

        MvcResult result = mockMvc.perform(post(this.URL + "/toggle")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Bearer " + userTest2.tokens().token())
        ).andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturnNotFoundBecauseCommentReport() throws Exception {
        ResponseUserTest superAdm = this.helper.loginSuperAdm();
        ReportTypeDTO reportTypeDTO = this.helper.createReportType(superAdm);

        ResponseUserTest userTest2 = this.helper.createUser();

        CreateCommentReportTypeDTO dto = new CreateCommentReportTypeDTO(
                1998780200074176609L,
                reportTypeDTO.id()
        );

        MvcResult result = mockMvc.perform(post(this.URL + "/toggle")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Bearer " + userTest2.tokens().token())
        ).andExpect(status().isNotFound()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturnNotFoundBecauseReportType() throws Exception {

        ResponseUserTest userTest = this.helper.createUser();
        ResponseUserTest userTest2 = this.helper.createUser();

        PostDTO postDTO = this.helper.createPost(userTest);
        CommentDTO commentDTO = this.helper.createComment(userTest, postDTO, null);

        CommentReportDTO commentReportDTO = this.helper.addReportToComment(userTest2, commentDTO);

        CreateCommentReportTypeDTO dto = new CreateCommentReportTypeDTO(
                commentReportDTO.id(),
                1998780200074176609L
        );

        MvcResult result = mockMvc.perform(post(this.URL + "/toggle")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Bearer " + userTest2.tokens().token())
        ).andExpect(status().isNotFound()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isFalse();

        assertThat(response.data()).isNull();
    }

}
