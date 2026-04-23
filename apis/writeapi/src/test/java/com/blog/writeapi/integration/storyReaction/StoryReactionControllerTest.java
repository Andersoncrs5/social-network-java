package com.blog.writeapi.integration.storyReaction;

import com.blog.writeapi.configs.HelperTest;
import com.blog.writeapi.configs.TestContainerConfig;
import com.blog.writeapi.modules.reaction.dtos.ReactionDTO;
import com.blog.writeapi.modules.stories.dto.StoryDTO;
import com.blog.writeapi.modules.storyReaction.dto.StoryReactionDTO;
import com.blog.writeapi.modules.storyReaction.dto.ToggleStoryReactionDTO;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.res.ResponseUserTest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class StoryReactionControllerTest {
    private final String URL = "/v1/story-reaction";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HelperTest helper;

    @Test
    void shouldAddReactionToStory() throws Exception {
        ResponseUserTest userTest = this.helper.createUser();
        ResponseUserTest adm = helper.loginSuperAdm();
        var traceId = UUID.randomUUID().toString();

        StoryDTO storyDTO = this.helper.createStory(userTest);
        ReactionDTO reactionDTO = this.helper.createReaction(adm);

        ToggleStoryReactionDTO dto = new ToggleStoryReactionDTO(
                reactionDTO.id(),
                storyDTO.getId()
        );

        MvcResult result = this.mockMvc.perform(post(URL+"/toggle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Bearer " + userTest.tokens().token())
                .header("X-Idempotency-Key", traceId)
        ).andExpect(status().isCreated()).andReturn();

        String json = result.getResponse().getContentAsString();
        ResponseHttp<StoryReactionDTO> response = objectMapper.readValue(json, new TypeReference<>() {});

        assertThat(response.data()).isNotNull();
        assertThat(response.status()).isTrue();
        assertThat(response.traceId()).isNotBlank().isEqualTo(traceId);
        assertThat(response.message()).isNotBlank();

        assertThat(response.data().id()).isNotZero();
        assertThat(response.data().user().id()).isNotZero().isEqualTo(userTest.userDTO().id());
        assertThat(response.data().story().getId()).isNotZero().isEqualTo(storyDTO.getId());
        assertThat(response.data().reaction().id()).isNotZero().isEqualTo(reactionDTO.id());
    }

    @Test
    void shouldRemoveReactionToStory() throws Exception {
        ResponseUserTest userTest = this.helper.createUser();
        ResponseUserTest adm = helper.loginSuperAdm();
        var traceId = UUID.randomUUID().toString();

        StoryDTO storyDTO = this.helper.createStory(userTest);
        ReactionDTO reactionDTO = this.helper.createReaction(adm);
        StoryReactionDTO storyReaction = this.helper.createStoryReaction(userTest, storyDTO, reactionDTO);

        ToggleStoryReactionDTO dto = new ToggleStoryReactionDTO(
                reactionDTO.id(),
                storyDTO.getId()
        );

        MvcResult result = this.mockMvc.perform(post(URL+"/toggle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Bearer " + userTest.tokens().token())
                .header("X-Idempotency-Key", traceId)
        ).andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        ResponseHttp<StoryReactionDTO> response = objectMapper.readValue(json, new TypeReference<>() {});

        assertThat(response.data()).isNull();
        assertThat(response.status()).isTrue();
        assertThat(response.traceId()).isNotBlank().isEqualTo(traceId);
        assertThat(response.message()).isNotBlank();
    }

    @Test
    void shouldUpdateReactionToStory() throws Exception {
        ResponseUserTest userTest = this.helper.createUser();
        ResponseUserTest adm = helper.loginSuperAdm();
        var traceId = UUID.randomUUID().toString();

        StoryDTO storyDTO = this.helper.createStory(userTest);
        ReactionDTO reactionDTO = this.helper.createReaction(adm);
        ReactionDTO reactionDTO2 = this.helper.createReaction(adm);
        StoryReactionDTO storyReaction = this.helper.createStoryReaction(userTest, storyDTO, reactionDTO);

        ToggleStoryReactionDTO dto = new ToggleStoryReactionDTO(
                reactionDTO2.id(),
                storyDTO.getId()
        );

        MvcResult result = this.mockMvc.perform(post(URL+"/toggle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Bearer " + userTest.tokens().token())
                .header("X-Idempotency-Key", traceId)
        ).andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        ResponseHttp<StoryReactionDTO> response = objectMapper.readValue(json, new TypeReference<>() {});

        assertThat(response.data()).isNotNull();
        assertThat(response.status()).isTrue();
        assertThat(response.traceId()).isNotBlank().isEqualTo(traceId);
        assertThat(response.message()).isNotBlank();

        assertThat(response.data().id()).isNotZero().isEqualTo(storyReaction.id());
        assertThat(response.data().user().id()).isNotZero().isEqualTo(userTest.userDTO().id());
        assertThat(response.data().story().getId()).isNotZero().isEqualTo(storyDTO.getId());
        assertThat(response.data().reaction().id()).isNotZero().isEqualTo(reactionDTO2.id());
    }

    @Test
    void shouldReturnNotFoundBecauseStoryAddReactionToStory() throws Exception {
        ResponseUserTest userTest = this.helper.createUser();
        ResponseUserTest adm = helper.loginSuperAdm();
        var traceId = UUID.randomUUID().toString();

        ReactionDTO reactionDTO = this.helper.createReaction(adm);

        ToggleStoryReactionDTO dto = new ToggleStoryReactionDTO(
                reactionDTO.id(),
                (reactionDTO.id() + 1L)
        );

        MvcResult result = this.mockMvc.perform(post(URL+"/toggle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Bearer " + userTest.tokens().token())
                .header("X-Idempotency-Key", traceId)
        ).andExpect(status().isNotFound()).andReturn();

        String json = result.getResponse().getContentAsString();
        ResponseHttp<Void> response = objectMapper.readValue(json, new TypeReference<>() {});

        assertThat(response.data()).isNull();
        assertThat(response.status()).isFalse();
        assertThat(response.traceId()).isNotBlank();
        assertThat(response.message()).isNotBlank();
    }

    @Test
    void shouldReturnNotFoundBecauseReactionAddReactionToStory() throws Exception {
        ResponseUserTest userTest = this.helper.createUser();
        var traceId = UUID.randomUUID().toString();

        StoryDTO storyDTO = this.helper.createStory(userTest);

        ToggleStoryReactionDTO dto = new ToggleStoryReactionDTO(
                storyDTO.getId(),
                storyDTO.getId()
        );

        MvcResult result = this.mockMvc.perform(post(URL+"/toggle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Bearer " + userTest.tokens().token())
                .header("X-Idempotency-Key", traceId)
        ).andExpect(status().isNotFound()).andReturn();

        String json = result.getResponse().getContentAsString();
        ResponseHttp<StoryReactionDTO> response = objectMapper.readValue(json, new TypeReference<>() {});

        assertThat(response.data()).isNull();
        assertThat(response.status()).isFalse();
        assertThat(response.traceId()).isNotBlank();
        assertThat(response.message()).isNotBlank();
    }

}
