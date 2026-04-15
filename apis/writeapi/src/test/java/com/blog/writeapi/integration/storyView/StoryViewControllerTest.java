package com.blog.writeapi.integration.storyView;

import com.blog.writeapi.configs.HelperTest;
import com.blog.writeapi.configs.TestContainerConfig;
import com.blog.writeapi.modules.stories.dto.StoryDTO;
import com.blog.writeapi.modules.storyView.repository.StoryViewRepository;
import com.blog.writeapi.modules.user.repository.UserRepository;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestContainerConfig.class)
public class StoryViewControllerTest {
    private final String URL = "/v1/story-view";

    @Autowired
    private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private StoryViewRepository repository;

    @Autowired
    private HelperTest helper;

    @BeforeEach
    void setup () {
        this.repository.deleteAll();
    }

    @Test
    void shouldCreateStoryView() throws Exception {
        ResponseUserTest userTest = this.helper.createUser();
        StoryDTO storyDTO = this.helper.createStory(userTest);
        var traceId = UUID.randomUUID().toString();

        MvcResult result = mockMvc.perform(post(this.URL + "/" +storyDTO.getId())
                .header("Authorization", "Bearer " + userTest.tokens().token())
                .header("X-Idempotency-Key", traceId)
        ).andExpect(status().isCreated()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Void>> typeRef = new TypeReference<>() {};

        ResponseHttp<Void> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank().isEqualTo(traceId);
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturnNotFoundWhenCreateStoryView() throws Exception {
        ResponseUserTest userTest = this.helper.createUser();
        var traceId = UUID.randomUUID().toString();

        MvcResult result = mockMvc.perform(post(this.URL + "/" + userTest.userDTO().id())
                .header("Authorization", "Bearer " + userTest.tokens().token())
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
    void shouldReturnOkBecauseStoryViewAlreadyExists() throws Exception {
        ResponseUserTest userTest = this.helper.createUser();
        StoryDTO storyDTO = this.helper.createStory(userTest);
        this.helper.createStoryView(storyDTO, userTest);
        var traceId = UUID.randomUUID().toString();

        MvcResult result = mockMvc.perform(post(this.URL + "/" +storyDTO.getId())
                .header("Authorization", "Bearer " + userTest.tokens().token())
                .header("X-Idempotency-Key", traceId)
        ).andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Void>> typeRef = new TypeReference<>() {};

        ResponseHttp<Void> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank().isEqualTo(traceId);
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data()).isNull();
    }

}
