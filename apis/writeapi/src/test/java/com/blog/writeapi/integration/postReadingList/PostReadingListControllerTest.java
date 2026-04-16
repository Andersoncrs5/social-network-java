package com.blog.writeapi.integration.postReadingList;

import com.blog.writeapi.configs.HelperTest;
import com.blog.writeapi.configs.TestContainerConfig;
import com.blog.writeapi.modules.post.dtos.PostDTO;
import com.blog.writeapi.modules.post.repository.PostRepository;
import com.blog.writeapi.modules.userReport.dto.UserReportDTO;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestContainerConfig.class)
public class PostReadingListControllerTest {

    private final String URL = "/v1/post-reading-list/";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private HelperTest helper;

    @BeforeEach
    void setup() {
        this.postRepository.deleteAll();
    }

    @Test
    void shouldAddedPostInReadingList() throws Exception {
        var traceId = UUID.randomUUID().toString();
        ResponseUserTest userTest = this.helper.createUser();
        PostDTO post = this.helper.createPost(userTest);

        MvcResult result = mockMvc.perform(post(this.URL + "toggle/" + post.id())
                        .header("Authorization", "Bearer " + userTest.tokens().token())
                        .header("X-Idempotency-Key", traceId)
                ).andExpect(status().isCreated()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Void>> typeRef = new TypeReference<>() {};

        ResponseHttp<Void> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.traceId()).isNotBlank().isEqualTo(traceId);
        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.traceId()).isNotBlank();
    }

    @Test
    void shouldRemovedPostInReadingList() throws Exception {
        var traceId = UUID.randomUUID().toString();
        ResponseUserTest userTest = this.helper.createUser();
        PostDTO post = this.helper.createPost(userTest);
        this.helper.addedPostToReading(userTest, post.id());

        MvcResult result = mockMvc.perform(post(this.URL + "toggle/" + post.id())
                        .header("Authorization", "Bearer " + userTest.tokens().token())
                        .header("X-Idempotency-Key", traceId)
                ).andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Void>> typeRef = new TypeReference<>() {};

        ResponseHttp<Void> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.traceId()).isNotBlank().isEqualTo(traceId);
        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.traceId()).isNotBlank();
    }

    @Test
    void shouldReturnNotFoundInEndpointToggle() throws Exception {
        var traceId = UUID.randomUUID().toString();
        ResponseUserTest userTest = this.helper.createUser();

        MvcResult result = mockMvc.perform(post(this.URL + "toggle/" + userTest.userDTO().id())
                        .header("Authorization", "Bearer " + userTest.tokens().token())
                        .header("X-Idempotency-Key", traceId)
                ).andExpect(status().isNotFound()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Void>> typeRef = new TypeReference<>() {};

        ResponseHttp<Void> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);
        assertThat(response.traceId()).isNotBlank();
    }

}
