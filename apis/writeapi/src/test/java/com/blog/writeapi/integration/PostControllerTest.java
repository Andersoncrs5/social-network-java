package com.blog.writeapi.integration;

import cn.hutool.core.lang.UUID;
import com.blog.writeapi.configs.HelperTest;
import com.blog.writeapi.configs.TestContainerConfig;
import com.blog.writeapi.modules.post.dtos.CreatePostDTO;
import com.blog.writeapi.modules.post.dtos.PostDTO;
import com.blog.writeapi.modules.post.dtos.UpdatePostDTO;
import com.blog.writeapi.modules.post.repository.PostRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestContainerConfig.class)
public class PostControllerTest {
    private final String URL = "/v1/post";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostRepository repository;

    @Autowired
    private HelperTest helper;

    @BeforeEach
    void setup () {
        this.repository.deleteAll();
    }

    // CREATE
    @Test
    void shouldCreateAPost() throws Exception {
        ResponseUserTest userData = this.helper.createUser();

        this.helper.createPost(userData);
    }

    @Test
    void shouldCreateAPostWithPostParent() throws Exception {
        var traceId = UUID.randomUUID().toString();
        ResponseUserTest userData = this.helper.createUser();
        PostDTO postDTO = this.helper.createPost(userData);

        CreatePostDTO dto = this.helper.createPostDTO(postDTO.id());

        MvcResult result = this.mockMvc.perform(post(this.URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token())
                        .header("X-Idempotency-Key", traceId)
                )
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isEqualTo(traceId);
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().title()).isEqualTo(dto.title());
        assertThat(response.data().slug()).isEqualTo(dto.slug());
        assertThat(response.data().content()).isEqualTo(dto.content());
        assertThat(response.data().readingTime()).isEqualTo(dto.readingTime());

        assertThat(response.data().author().id()).isEqualTo(userData.userDTO().id());
        assertThat(response.data().author().id()).isEqualTo(userData.userDTO().id());
    }

    // GET
    @Test
    void shouldGetAPost() throws Exception {

        ResponseUserTest userData = this.helper.createUser();
        PostDTO post = this.helper.createPost(userData);

        MvcResult result = this.mockMvc.perform(get(this.URL + "/" + post.id())
                .header("Authorization", "Bearer " + userData.tokens().token()))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isEqualTo(post.id());
    }

    @Test
    void shouldReturnNotFoundWhenGetPostById() throws Exception {

        ResponseUserTest userData = this.helper.createUser();

        MvcResult result = this.mockMvc.perform(get(this.URL + "/" + 104657469563746526L)
                        .header("Authorization", "Bearer " + userData.tokens().token()))
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.data()).isNull();
        assertThat(response.status()).isEqualTo(false);
    }

    // DELETE
    @Test
    void shouldDeletePostById() throws Exception {
        var traceId = UUID.randomUUID().toString();

        ResponseUserTest userData = this.helper.createUser();
        PostDTO post = this.helper.createPost(userData);

        MvcResult result = this.mockMvc.perform(delete(this.URL + "/" + post.id())
                        .header("Authorization", "Bearer " + userData.tokens().token())
                        .header("X-Idempotency-Key", traceId)
                )
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.traceId()).isEqualTo(traceId);
        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturnForbWhenDeletePostById() throws Exception {
        var traceId = UUID.randomUUID().toString();

        ResponseUserTest userData = this.helper.createUser();
        ResponseUserTest userData2 = this.helper.createUser();
        PostDTO post = this.helper.createPost(userData);

        this.mockMvc.perform(delete(this.URL + "/" + post.id())
                        .header("Authorization", "Bearer " + userData2.tokens().token())
                        .header("X-Idempotency-Key", traceId))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    // UPDATE
    @Test
    void shouldReturnPostUpdate() throws Exception {
        var traceId = UUID.randomUUID().toString();

        ResponseUserTest userData = this.helper.createUser();
        PostDTO post = this.helper.createPost(userData);

        UpdatePostDTO dto = new UpdatePostDTO(
                "updated post with simple message",
                "updated-post-with-simple-message",
                """
                        aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                        aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                        aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                        aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                        aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                        aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                        aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                        aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                        aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                        """,
                12
        );

        MvcResult result = mockMvc.perform(patch(this.URL + "/" + post.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token())
                        .header("X-Idempotency-Key", traceId)
                )
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.traceId()).isEqualTo(traceId);
        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isEqualTo(post.id());
        assertThat(response.data().content()).isEqualTo(dto.content());
        assertThat(response.data().title()).isEqualTo(dto.title());
        assertThat(response.data().readingTime()).isEqualTo(dto.readingTime());
        assertThat(response.data().slug()).isEqualTo(dto.slug());
        assertThat(response.data().createdAt().getSecond()).isEqualTo(post.createdAt().getSecond());
    }

    @Test
    void shouldReturnPostUpdateJustTitle() throws Exception {
        var traceId = UUID.randomUUID().toString();

        ResponseUserTest userData = this.helper.createUser();
        PostDTO post = this.helper.createPost(userData);

        UpdatePostDTO dto = new UpdatePostDTO(
                "updated post with simple message",
                null,
                null,
                null
        );

        MvcResult result = mockMvc.perform(patch(this.URL + "/" + post.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token())
                        .header("X-Idempotency-Key", traceId)
                ).andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isEqualTo(traceId);
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isEqualTo(post.id());
        assertThat(response.data().content()).isEqualTo(post.content());
        assertThat(response.data().title()).isEqualTo(dto.title());
        assertThat(response.data().readingTime()).isEqualTo(post.readingTime());
        assertThat(response.data().slug()).isEqualTo(post.slug());
        assertThat(response.data().createdAt().getSecond()).isEqualTo(post.createdAt().getSecond());
    }

    @Test
    void shouldReturnPostUpdateJustSlug() throws Exception {
        var traceId = UUID.randomUUID().toString();

        ResponseUserTest userData = this.helper.createUser();
        PostDTO post = this.helper.createPost(userData);

        UpdatePostDTO dto = new UpdatePostDTO(
                null,
                "new-slug-updated",
                null,
                null
        );

        MvcResult result = mockMvc.perform(patch(this.URL + "/" + post.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token())
                        .header("X-Idempotency-Key", traceId)
                )
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.traceId()).isEqualTo(traceId);
        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isEqualTo(post.id());
        assertThat(response.data().content()).isEqualTo(post.content());
        assertThat(response.data().title()).isEqualTo(post.title());
        assertThat(response.data().readingTime()).isEqualTo(post.readingTime());
        assertThat(response.data().slug()).isEqualTo(dto.slug());
        assertThat(response.data().createdAt().getSecond()).isEqualTo(post.createdAt().getSecond());
    }

    @Test
    void shouldReturnPostUpdateJustContent() throws Exception {
        var traceId = UUID.randomUUID().toString();

        ResponseUserTest userData = this.helper.createUser();
        PostDTO post = this.helper.createPost(userData);

        UpdatePostDTO dto = new UpdatePostDTO(
                null,
                null,
                """
                        aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                        aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                        aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                        aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                        aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                        aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                        aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                        aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                        aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                        """,
                null
        );

        MvcResult result = mockMvc.perform(patch(this.URL + "/" + post.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token())
                        .header("X-Idempotency-Key", traceId)
                )
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.traceId()).isNotBlank().isEqualTo(traceId);
        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isEqualTo(post.id());
        assertThat(response.data().content()).isEqualTo(dto.content());
        assertThat(response.data().title()).isEqualTo(post.title());
        assertThat(response.data().readingTime()).isEqualTo(post.readingTime());
        assertThat(response.data().slug()).isEqualTo(post.slug());
        assertThat(response.data().createdAt().getSecond()).isEqualTo(post.createdAt().getSecond());
    }

    @Test
    void shouldReturnPostUpdateJustReadingTime() throws Exception {
        var traceId = UUID.randomUUID().toString();

        ResponseUserTest userData = this.helper.createUser();
        PostDTO post = this.helper.createPost(userData);

        UpdatePostDTO dto = new UpdatePostDTO(
                null,
                null,
                null,
                9
        );

        MvcResult result = mockMvc.perform(patch(this.URL + "/" + post.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token())
                        .header("X-Idempotency-Key", traceId)
                )
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.traceId()).isNotBlank().isEqualTo(traceId);
        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isEqualTo(post.id());
        assertThat(response.data().content()).isEqualTo(post.content());
        assertThat(response.data().title()).isEqualTo(post.title());
        assertThat(response.data().readingTime()).isEqualTo(dto.readingTime());
        assertThat(response.data().slug()).isEqualTo(post.slug());
        assertThat(response.data().createdAt().getSecond()).isEqualTo(post.createdAt().getSecond());
    }

    @Test
    void shouldReturnForbBecauseAnotherUserTriedUpdatePost() throws Exception {
        var traceId = UUID.randomUUID().toString();

        ResponseUserTest userData = this.helper.createUser();
        ResponseUserTest userData2 = this.helper.createUser();

        PostDTO post = this.helper.createPost(userData);

        UpdatePostDTO dto = new UpdatePostDTO(
                null,
                null,
                null,
                9
        );

        MvcResult result = mockMvc.perform(patch(this.URL + "/" + post.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData2.tokens().token())
                        .header("X-Idempotency-Key", traceId)
                )
                .andExpect(status().isForbidden())
                .andReturn();
    }


}
