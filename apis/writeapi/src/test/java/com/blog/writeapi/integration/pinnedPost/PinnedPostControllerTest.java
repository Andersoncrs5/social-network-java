package com.blog.writeapi.integration.pinnedPost;

import com.blog.writeapi.configs.HelperTest;
import com.blog.writeapi.configs.TestContainerConfig;
import com.blog.writeapi.modules.pinnedPost.dto.CreatePinnedPostDTO;
import com.blog.writeapi.modules.pinnedPost.dto.PinnedPostDTO;
import com.blog.writeapi.modules.pinnedPost.dto.UpdatePinnedPostDTO;
import com.blog.writeapi.modules.pinnedPost.repository.PinnedPostRepository;
import com.blog.writeapi.modules.post.dtos.PostDTO;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestContainerConfig.class)
public class PinnedPostControllerTest {
    private final String URL = "/v1/pinned-post";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired private PostRepository postRepository;

    @Autowired
    private HelperTest helper;

    @BeforeEach
    void setup () {
        this.postRepository.deleteAll();
    }

    @Test
    void shouldCreatePinnedToPost() throws Exception {
        var traceId = UUID.randomUUID().toString();

        ResponseUserTest userData = this.helper.createUser();
        PostDTO post = this.helper.createPost(userData);

        CreatePinnedPostDTO dto = new CreatePinnedPostDTO(
                post.id(),
                1
        );

        MvcResult result = this.mockMvc.perform(post(this.URL)
                        .header("Authorization", "Bearer " + userData.tokens().token())
                        .header("X-Idempotency-Key", traceId)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isCreated()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PinnedPostDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PinnedPostDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank().isEqualTo(traceId);
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isNotNull().isPositive();
        assertThat(response.data().post().id()).isEqualTo(post.id());
        assertThat(response.data().user().id()).isEqualTo(userData.userDTO().id());
        assertThat(response.data().orderIndex()).isEqualTo(dto.orderIndex());

    }

    @Test
    void shouldReturnConflictUserAlreadyMarkedPost() throws Exception {
        var traceId = UUID.randomUUID().toString();

        ResponseUserTest userData = this.helper.createUser();
        PostDTO post = this.helper.createPost(userData);
        PinnedPostDTO pinnedPostDTO = this.helper.markPostWithPinned(userData, post);

        CreatePinnedPostDTO dto = new CreatePinnedPostDTO(
                post.id(),
                pinnedPostDTO.orderIndex() + 1
        );

        MvcResult result = this.mockMvc.perform(post(this.URL)
                        .header("Authorization", "Bearer " + userData.tokens().token())
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Idempotency-Key", traceId)
                ).andExpect(status().isConflict()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PinnedPostDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PinnedPostDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturnConflictUserAlreadyMarkedPostWithOrderEquals() throws Exception {
        var traceId = UUID.randomUUID().toString();

        ResponseUserTest userData = this.helper.createUser();
        PostDTO post = this.helper.createPost(userData);
        PostDTO post2 = this.helper.createPost(userData);
        PinnedPostDTO pinnedPostDTO = this.helper.markPostWithPinned(userData, post);

        CreatePinnedPostDTO dto = new CreatePinnedPostDTO(
                post2.id(),
                pinnedPostDTO.orderIndex()
        );

        MvcResult result = this.mockMvc.perform(post(this.URL)
                        .header("Authorization", "Bearer " + userData.tokens().token())
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Idempotency-Key", traceId)
                ).andExpect(status().isConflict()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PinnedPostDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PinnedPostDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturnNotFoundBecausePostNotFoundCreatePinnedToPost() throws Exception {
        var traceId = UUID.randomUUID().toString();

        ResponseUserTest userData = this.helper.createUser();

        CreatePinnedPostDTO dto = new CreatePinnedPostDTO(
                userData.userDTO().id(),
                1
        );

        MvcResult result = this.mockMvc.perform(post(this.URL)
                        .header("Authorization", "Bearer " + userData.tokens().token())
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Idempotency-Key", traceId)
                ).andExpect(status().isNotFound()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PinnedPostDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PinnedPostDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldDeletePinned() throws Exception {
        var traceId = UUID.randomUUID().toString();

        ResponseUserTest userData = this.helper.createUser();
        PostDTO post = this.helper.createPost(userData);
        PinnedPostDTO pinnedPostDTO = this.helper.markPostWithPinned(userData, post);

        MvcResult result = this.mockMvc.perform(delete(this.URL + "/" + pinnedPostDTO.id())
                .header("Authorization", "Bearer " + userData.tokens().token())
                .header("X-Idempotency-Key", traceId)
        ).andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Void>> typeRef = new TypeReference<>() {};

        ResponseHttp<Void> response = objectMapper.readValue(json, typeRef);

        assertThat(response.traceId()).isNotBlank().isEqualTo(traceId);
        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturnNotFoundDeletePinned() throws Exception {
        var traceId = UUID.randomUUID().toString();
        ResponseUserTest userData = this.helper.createUser();

        MvcResult result = this.mockMvc.perform(delete(this.URL + "/" + userData.userDTO().id())
                .header("Authorization", "Bearer " + userData.tokens().token())
                .header("X-Idempotency-Key", traceId)
        ).andExpect(status().isNotFound()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Void>> typeRef = new TypeReference<>() {};

        ResponseHttp<Void> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturnNotFoundPatchPinned() throws Exception {
        var traceId = UUID.randomUUID().toString();
        ResponseUserTest userData = this.helper.createUser();

        UpdatePinnedPostDTO dto = new UpdatePinnedPostDTO(
                1
        );

        MvcResult result = this.mockMvc.perform(patch(this.URL + "/" + userData.userDTO().id())
                .header("Authorization", "Bearer " + userData.tokens().token())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header("X-Idempotency-Key", traceId)
        ).andExpect(status().isNotFound()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Void>> typeRef = new TypeReference<>() {};

        ResponseHttp<Void> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldPatchPinned() throws Exception {
        var traceId = UUID.randomUUID().toString();
        ResponseUserTest userData = this.helper.createUser();
        PostDTO post = this.helper.createPost(userData);
        PinnedPostDTO pinnedPostDTO = this.helper.markPostWithPinned(userData, post);

        UpdatePinnedPostDTO dto = new UpdatePinnedPostDTO(
                (pinnedPostDTO.orderIndex() + 1)
        );

        MvcResult result = this.mockMvc.perform(patch(this.URL + "/" + pinnedPostDTO.id())
                .header("Authorization", "Bearer " + userData.tokens().token())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header("X-Idempotency-Key", traceId)
        ).andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PinnedPostDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PinnedPostDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.traceId()).isNotBlank().isEqualTo(traceId);
        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data()).isNotNull();
        assertThat(response.data().id()).isEqualTo(pinnedPostDTO.id());
        assertThat(response.data().post().id()).isEqualTo(pinnedPostDTO.post().id());
        assertThat(response.data().user().id()).isEqualTo(pinnedPostDTO.user().id());
        assertThat(response.data().orderIndex()).isEqualTo(dto.orderIndex());
    }

}
