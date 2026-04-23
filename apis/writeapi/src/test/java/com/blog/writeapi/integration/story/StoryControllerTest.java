package com.blog.writeapi.integration.story;

import cn.hutool.core.lang.UUID;
import com.blog.writeapi.configs.HelperTest;
import com.blog.writeapi.configs.TestContainerConfig;
import com.blog.writeapi.modules.stories.dto.StoryDTO;
import com.blog.writeapi.modules.stories.repository.StoryRepository;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestContainerConfig.class)
public class StoryControllerTest {
    private final String URL = "/v1/story";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired private StoryRepository repository;

    @Autowired
    private HelperTest helper;

    @BeforeEach
    void setup () {
        this.repository.deleteAll();
    }

    @Test
    void shouldReturnConflictWhenSameIdempotencyKeyIsUsed() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        StoryDTO story = this.helper.createStory(userData);
        String traceId = UUID.randomUUID().toString();

        mockMvc.perform(delete(this.URL + "/" + story.getId())
                .header("Authorization", "Bearer " + userData.tokens().token())
                .header("X-Idempotency-Key", traceId)
        ).andExpect(status().isOk());

        mockMvc.perform(delete(this.URL + "/" + story.getId())
                .header("Authorization", "Bearer " + userData.tokens().token())
                .header("X-Idempotency-Key", traceId)
        ).andExpect(status().isConflict());
    }

    @Test
    void shouldCreateNewStory() throws Exception {
        ResponseUserTest userData = this.helper.createUser();

        this.helper.createStory(userData);
    }

    @Test
    void shouldDeleteStory() throws Exception {
        var traceId = UUID.randomUUID().toString();
        ResponseUserTest userData = this.helper.createUser();
        StoryDTO story = this.helper.createStory(userData);

        MvcResult result = mockMvc.perform(delete(this.URL + "/" + story.getId())
                .header("Authorization", "Bearer " + userData.tokens().token())
                .header("X-Idempotency-Key",traceId )
        ).andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        ResponseHttp<Void> response = objectMapper.readValue(json, new TypeReference<>() {});

        assertThat(response.data()).isNull();
        assertThat(response.status()).isTrue();
        assertThat(response.traceId()).isNotBlank().isEqualTo(traceId);
    }

    @Test
    void shouldReturnForbBecauseIdempotencyKeyMissedDeleteStory() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        StoryDTO story = this.helper.createStory(userData);

        MvcResult result = mockMvc.perform(delete(this.URL + "/" + story.getId())
                .header("Authorization", "Bearer " + userData.tokens().token())
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    void shouldReturnNotFoundDeleteStory() throws Exception {
        ResponseUserTest userData = this.helper.createUser();

        MvcResult result = mockMvc.perform(delete(this.URL + "/" + userData.userDTO().id())
                .header("Authorization", "Bearer " + userData.tokens().token())
                .header("X-Idempotency-Key", UUID.randomUUID().toString())
        ).andExpect(status().isNotFound()).andReturn();

        String json = result.getResponse().getContentAsString();
        ResponseHttp<Void> response = objectMapper.readValue(json, new TypeReference<>() {});

        assertThat(response.data()).isNull();
        assertThat(response.status()).isFalse();
        assertThat(response.traceId()).isNotBlank();
        assertThat(response.message()).isNotBlank();
    }

}
