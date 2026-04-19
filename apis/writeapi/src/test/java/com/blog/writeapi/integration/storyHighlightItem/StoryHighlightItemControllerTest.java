package com.blog.writeapi.integration.storyHighlightItem;

import com.blog.writeapi.configs.HelperTest;
import com.blog.writeapi.configs.TestContainerConfig;
import com.blog.writeapi.modules.StoryHighlightItem.dto.CreateStoryHighlightItemDTO;
import com.blog.writeapi.modules.StoryHighlightItem.repository.StoryHighlightItemRepository;
import com.blog.writeapi.modules.stories.dto.StoryDTO;
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
public class StoryHighlightItemControllerTest {
    private final String URL = "/v1/story-highlight-item";

    @Autowired
    private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private StoryHighlightRepository repository;

    @Autowired private HelperTest helper;

    @BeforeEach
    void setup () {
        this.repository.deleteAll();
    }

    @Test
    void shouldDeleteStoryInHighlight() throws Exception {
        var traceId = UUID.randomUUID().toString();
        ResponseUserTest userData = this.helper.createUser();

        StoryDTO storyDTO = this.helper.createStory(userData);
        StoryHighlightDTO storyHighlightDTO = this.helper.createStoryHighlight(userData);
        this.helper.createStoryHighlightItem(storyHighlightDTO, storyDTO, userData);

        var dto = new CreateStoryHighlightItemDTO(
                storyHighlightDTO.getId(),
                storyDTO.getId()
        );

        MvcResult result = this.mockMvc.perform(post(this.URL + "/toggle")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Bearer " + userData.tokens().token())
                .header("X-Idempotency-Key", traceId)
        ).andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        ResponseHttp<Void> response = objectMapper.readValue(json, new TypeReference<>() {});

        assertThat(response.data()).isNull();
        assertThat(response.status()).isTrue();
        assertThat(response.message()).isNotBlank().containsIgnoringCase("removed");
        assertThat(response.traceId()).isNotBlank().isEqualTo(traceId);
    }

    @Test
    void shouldCreateStoryInHighlight() throws Exception {
        var traceId = UUID.randomUUID().toString();
        ResponseUserTest userData = this.helper.createUser();

        StoryDTO storyDTO = this.helper.createStory(userData);
        StoryHighlightDTO storyHighlightDTO = this.helper.createStoryHighlight(userData);

        var dto = new CreateStoryHighlightItemDTO(
                storyHighlightDTO.getId(),
                storyDTO.getId()
        );

        MvcResult result = this.mockMvc.perform(post(this.URL + "/toggle")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Bearer " + userData.tokens().token())
                .header("X-Idempotency-Key", traceId)
        ).andExpect(status().isCreated()).andReturn();

        String json = result.getResponse().getContentAsString();
        ResponseHttp<Void> response = objectMapper.readValue(json, new TypeReference<>() {});

        assertThat(response.data()).isNull();
        assertThat(response.status()).isTrue();
        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank().isEqualTo(traceId);
    }

    @Test
    void shouldReturnNotFoundBecauseHighlightCreateStoryInHighlight() throws Exception {
        var traceId = UUID.randomUUID().toString();
        ResponseUserTest userData = this.helper.createUser();

        StoryDTO storyDTO = this.helper.createStory(userData);

        var dto = new CreateStoryHighlightItemDTO(
                storyDTO.getId(),
                storyDTO.getId()
        );

        MvcResult result = this.mockMvc.perform(post(this.URL + "/toggle")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Bearer " + userData.tokens().token())
                .header("X-Idempotency-Key", traceId)
        ).andExpect(status().isNotFound()).andReturn();

        String json = result.getResponse().getContentAsString();
        ResponseHttp<Void> response = objectMapper.readValue(json, new TypeReference<>() {});

        assertThat(response.data()).isNull();
        assertThat(response.status()).isFalse();
        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();
    }

    @Test
    void shouldReturnNotFoundBecauseStoryCreateStoryInHighlight() throws Exception {
        var traceId = UUID.randomUUID().toString();
        ResponseUserTest userData = this.helper.createUser();

        StoryHighlightDTO storyHighlightDTO = this.helper.createStoryHighlight(userData);

        var dto = new CreateStoryHighlightItemDTO(
                storyHighlightDTO.getId(),
                (storyHighlightDTO.getId() + 1)
        );

        MvcResult result = this.mockMvc.perform(post(this.URL + "/toggle")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Bearer " + userData.tokens().token())
                .header("X-Idempotency-Key", traceId)
        ).andExpect(status().isNotFound()).andReturn();

        String json = result.getResponse().getContentAsString();
        ResponseHttp<Void> response = objectMapper.readValue(json, new TypeReference<>() {});

        assertThat(response.data()).isNull();
        assertThat(response.status()).isFalse();
        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();
    }

}
