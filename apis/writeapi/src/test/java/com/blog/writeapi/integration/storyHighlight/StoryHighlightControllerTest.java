package com.blog.writeapi.integration.storyHighlight;

import com.blog.writeapi.configs.HelperTest;
import com.blog.writeapi.configs.TestContainerConfig;
import com.blog.writeapi.modules.storyHighlight.dto.StoryHighlightDTO;
import com.blog.writeapi.modules.storyHighlight.repository.StoryHighlightRepository;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestContainerConfig.class)
public class StoryHighlightControllerTest {
    private final String URL = "/v1/story-highlight";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private StoryHighlightRepository repository;

    @Autowired private HelperTest helper;

    @BeforeEach
    void setup () {
        this.repository.deleteAll();
    }

    @Test
    void shouldCreateNewStoryHighlight() throws Exception {
        var traceId = UUID.randomUUID().toString();
        ResponseUserTest userData = this.helper.createUser();
        MockMultipartFile filePart = this.helper.loadFile();

        assertThat(filePart.getContentType()).isNotNull();

        var request = multipart(URL)
                .file(filePart)
                .param("title", "Título de Exemplo")
                .param("isPublic", "true")
                .param("isVisible", "true")
                .param("fileName", "pochita.png")
                .param("contentType", filePart.getContentType())
                .header("Authorization", "Bearer " + userData.tokens().token())
                .header("X-Idempotency-Key", traceId);

        MvcResult result = this.mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        ResponseHttp<StoryHighlightDTO> response = objectMapper.readValue(json, new TypeReference<>() {});

        assertThat(response.data()).isNotNull();
        assertThat(response.status()).isTrue();
        assertThat(response.traceId()).isNotBlank().isEqualTo(traceId);

        assertThat(response.data().getStorageKey()).isNotBlank();
        assertThat(response.data().getUser().id()).isEqualTo(userData.userDTO().id());
    }

    @Test
    void shouldReturnBadRequestIdempotencyKeyMissedWhenCreateNewStoryHighlight() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        MockMultipartFile filePart = this.helper.loadFile();

        assertThat(filePart.getContentType()).isNotNull();

        var request = multipart(URL)
                .file(filePart)
                .param("title", "Título de Exemplo")
                .param("isPublic", "true")
                .param("isVisible", "true")
                .param("fileName", "pochita.png")
                .param("contentType", filePart.getContentType())
                .header("Authorization", "Bearer " + userData.tokens().token());

        this.mockMvc.perform(request).andExpect(status().isBadRequest());
    }

    @Test
    void shouldDelete() throws Exception {
        var traceId = UUID.randomUUID().toString();
        ResponseUserTest userData = this.helper.createUser();

        StoryHighlightDTO storyHighlightDTO = this.helper.createStoryHighlight(userData);

        MvcResult result = this.mockMvc.perform(delete(URL + "/" + storyHighlightDTO.getId())
                .header("Authorization", "Bearer " + userData.tokens().token())
                .header("X-Idempotency-Key", traceId)
        ).andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        ResponseHttp<Void> response = objectMapper.readValue(json, new TypeReference<>() {});

        assertThat(response.data()).isNull();
        assertThat(response.status()).isTrue();
        assertThat(response.traceId()).isNotBlank().isEqualTo(traceId);

    }

    @Test
    void shouldReturnNotFoundDelete() throws Exception {
        var traceId = UUID.randomUUID().toString();
        ResponseUserTest userData = this.helper.createUser();

        MvcResult result = this.mockMvc.perform(delete(URL + "/" + userData.userDTO().id())
                .header("Authorization", "Bearer " + userData.tokens().token())
                .header("X-Idempotency-Key", traceId)
        ).andExpect(status().isNotFound()).andReturn();

        String json = result.getResponse().getContentAsString();
        ResponseHttp<Void> response = objectMapper.readValue(json, new TypeReference<>() {});

        assertThat(response.data()).isNull();
        assertThat(response.status()).isFalse();
        assertThat(response.traceId()).isNotBlank();

    }

    @Test
    void shouldReturnBAdRequestIdempotencyKeyBecauseDelete() throws Exception {
        ResponseUserTest userData = this.helper.createUser();

        StoryHighlightDTO storyHighlightDTO = this.helper.createStoryHighlight(userData);

        this.mockMvc.perform(delete(URL + "/" + storyHighlightDTO.getId())
                .header("Authorization", "Bearer " + userData.tokens().token())
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    void shouldUpdateStoryHighlight() throws Exception {
        var traceId = UUID.randomUUID().toString();
        ResponseUserTest userData = this.helper.createUser();
        MockMultipartFile filePart = this.helper.loadFile();
        StoryHighlightDTO storyHighlightDTO = this.helper.createStoryHighlight(userData);

        assertThat(filePart.getContentType()).isNotNull();

        var request = multipart(URL + "/" + storyHighlightDTO.getId())
                .file(filePart)
                .param("title", "Título de Exemplo Update")
                .param("isPublic", "false")
                .param("isVisible", "false")
                .param("fileName", "pochita-udpate.png")
                .param("contentType", filePart.getContentType())
                .header("Authorization", "Bearer " + userData.tokens().token())
                .header("X-Idempotency-Key", traceId)
                .with(req -> {
                    req.setMethod("PATCH");
                    return req;
                });

        MvcResult result = this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        ResponseHttp<StoryHighlightDTO> response = objectMapper.readValue(json, new TypeReference<>() {});

        assertThat(response.data()).isNotNull();
        assertThat(response.status()).isTrue();
        assertThat(response.traceId()).isNotBlank().isEqualTo(traceId);

        assertThat(response.data().getStorageKey()).isNotBlank();
        assertThat(response.data().getTitle()).isNotBlank().isEqualTo("Título de Exemplo Update");
        assertThat(response.data().getFileName()).isNotBlank().isEqualTo("pochita-udpate.png");
        assertThat(response.data().getIsPublic()).isFalse();
        assertThat(response.data().getIsVisible()).isFalse();
        assertThat(response.data().getUser().id()).isEqualTo(userData.userDTO().id());
    }

    @Test
    void shouldReturnNotFoundUpdateStoryHighlight() throws Exception {
        var traceId = UUID.randomUUID().toString();
        ResponseUserTest userData = this.helper.createUser();
        MockMultipartFile filePart = this.helper.loadFile();

        assertThat(filePart.getContentType()).isNotNull();

        var request = multipart(URL + "/" + userData.userDTO().id())
                .file(filePart)
                .param("title", "Título de Exemplo Update")
                .param("isPublic", "false")
                .param("isVisible", "false")
                .param("fileName", "pochita-udpate.png")
                .param("contentType", filePart.getContentType())
                .header("Authorization", "Bearer " + userData.tokens().token())
                .header("X-Idempotency-Key", traceId)
                .with(req -> {
                    req.setMethod("PATCH");
                    return req;
                });

        MvcResult result = this.mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        ResponseHttp<StoryHighlightDTO> response = objectMapper.readValue(json, new TypeReference<>() {});

        assertThat(response.data()).isNull();
        assertThat(response.status()).isFalse();
        assertThat(response.traceId()).isNotBlank();
    }

}
