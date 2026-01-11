package com.blog.writeapi.integration;

import com.blog.writeapi.HelperTest;
import com.blog.writeapi.dtos.reaction.CreateReactionDTO;
import com.blog.writeapi.dtos.reaction.ReactionDTO;
import com.blog.writeapi.dtos.reaction.UpdateReactionDTO;
import com.blog.writeapi.models.enums.reaction.ReactionTypeEnum;
import com.blog.writeapi.repositories.ReactionRepository;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ReactionControllerTest {

    private final String URL = "/v1/reaction";

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @Autowired private ReactionRepository repository;

    @Autowired
    private HelperTest helper;

    @BeforeEach
    void setup() {
        this.repository.deleteAll();
    }

    @Test
    void shouldCreateNewReaction() throws Exception {
        ResponseUserTest userData = helper.loginSuperAdm();

        CreateReactionDTO dto = new CreateReactionDTO(
                "emoji",
                null,
                "U+1F525",
                1L,
                true,
                true,
                ReactionTypeEnum.EMOTION
        );

        MvcResult result = this.mockMvc.perform(post(this.URL).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<ReactionDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<ReactionDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isNotNegative().isNotZero();
        assertThat(response.data().name()).isEqualTo(dto.name());
        assertThat(response.data().emojiUrl()).isEqualTo(dto.emojiUrl());
        assertThat(response.data().emojiUnicode()).isEqualTo(dto.emojiUnicode());
        assertThat(response.data().displayOrder()).isEqualTo(dto.displayOrder());
        assertThat(response.data().active()).isEqualTo(dto.active());
        assertThat(response.data().visible()).isEqualTo(dto.visible());
        assertThat(response.data().type()).isEqualTo(dto.type());
    }

    @Test
    void shouldDeleteReaction() throws Exception {
        ResponseUserTest userData = helper.loginSuperAdm();
        ReactionDTO reaction = this.helper.createReaction(userData);

        MvcResult result = this.mockMvc.perform(delete(this.URL + "/" + reaction.id())
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldUpdateAllFieldsReaction() throws Exception {
        ResponseUserTest userData = helper.loginSuperAdm();
        ReactionDTO reaction = this.helper.createReaction(userData);

        UpdateReactionDTO dto = new UpdateReactionDTO(
                reaction.name() + "a",
                "https://preview.redd.it/pochita-art-v0-ckhgpbbt06lc1.jpg?width=640&crop=smart&auto=webp&s=c28e059a9408e6c1aad6116b37233ea58ac1a239",
                "U+1F525",
                reaction.displayOrder() + 1,
                !reaction.active(),
                !reaction.visible(),
                ReactionTypeEnum.AWARD
        );

        MvcResult result = this.mockMvc.perform(patch(this.URL + "/" + reaction.id())
                        .content(this.objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<ReactionDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<ReactionDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isEqualTo(reaction.id());
        assertThat(response.data().name()).isEqualTo(dto.name());
        assertThat(response.data().emojiUrl()).isEqualTo(dto.emojiUrl());
        assertThat(response.data().emojiUnicode()).isEqualTo(dto.emojiUnicode());
        assertThat(response.data().displayOrder()).isEqualTo(dto.displayOrder());
        assertThat(response.data().active()).isEqualTo(dto.active());
        assertThat(response.data().visible()).isEqualTo(dto.visible());
        assertThat(response.data().type()).isEqualTo(dto.type());
    }

    @Test
    void shouldUpdateJustFieldNameReaction() throws Exception {
        ResponseUserTest userData = helper.loginSuperAdm();
        ReactionDTO reaction = this.helper.createReaction(userData);

        UpdateReactionDTO dto = new UpdateReactionDTO(
                reaction.name() + "a",
                null,
                null,
                null,
                null,
                null,
                null
        );

        MvcResult result = this.mockMvc.perform(patch(this.URL + "/" + reaction.id())
                        .content(this.objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<ReactionDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<ReactionDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isEqualTo(reaction.id());
        assertThat(response.data().name()).isEqualTo(dto.name());
        assertThat(response.data().emojiUrl()).isEqualTo(reaction.emojiUrl());
        assertThat(response.data().emojiUnicode()).isEqualTo(reaction.emojiUnicode());
        assertThat(response.data().displayOrder()).isEqualTo(reaction.displayOrder());
        assertThat(response.data().active()).isEqualTo(reaction.active());
        assertThat(response.data().visible()).isEqualTo(reaction.visible());
        assertThat(response.data().type()).isEqualTo(reaction.type());
    }

    @Test
    void shouldUpdateJustFieldEmojiUrlReaction() throws Exception {
        ResponseUserTest userData = helper.loginSuperAdm();
        ReactionDTO reaction = this.helper.createReaction(userData);

        UpdateReactionDTO dto = new UpdateReactionDTO(
                null,
                "https://preview.redd.it/pochita-art-v0-ckhgpbbt06lc1.jpg?width=640&crop=smart&auto=webp&s=c28e059a9408e6c1aad6116b37233ea58ac1a239",
                null,
                null,
                null,
                null,
                null
        );

        MvcResult result = this.mockMvc.perform(patch(this.URL + "/" + reaction.id())
                        .content(this.objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<ReactionDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<ReactionDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isEqualTo(reaction.id());
        assertThat(response.data().name()).isEqualTo(reaction.name());
        assertThat(response.data().emojiUrl()).isEqualTo(dto.emojiUrl());
        assertThat(response.data().emojiUnicode()).isEqualTo(reaction.emojiUnicode());
        assertThat(response.data().displayOrder()).isEqualTo(reaction.displayOrder());
        assertThat(response.data().active()).isEqualTo(reaction.active());
        assertThat(response.data().visible()).isEqualTo(reaction.visible());
        assertThat(response.data().type()).isEqualTo(reaction.type());
    }

    @Test
    void shouldUpdateJustFieldEmojiUnicodeReaction() throws Exception {
        ResponseUserTest userData = helper.loginSuperAdm();
        ReactionDTO reaction = this.helper.createReaction(userData);

        UpdateReactionDTO dto = new UpdateReactionDTO(
                null,
                null,
                "0x1F600",
                null,
                null,
                null,
                null
        );

        MvcResult result = this.mockMvc.perform(patch(this.URL + "/" + reaction.id())
                        .content(this.objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<ReactionDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<ReactionDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isEqualTo(reaction.id());
        assertThat(response.data().name()).isEqualTo(reaction.name());
        assertThat(response.data().emojiUrl()).isEqualTo(reaction.emojiUrl());
        assertThat(response.data().emojiUnicode()).isEqualTo(dto.emojiUnicode());
        assertThat(response.data().displayOrder()).isEqualTo(reaction.displayOrder());
        assertThat(response.data().active()).isEqualTo(reaction.active());
        assertThat(response.data().visible()).isEqualTo(reaction.visible());
        assertThat(response.data().type()).isEqualTo(reaction.type());
    }

    @Test
    void shouldUpdateJustFieldDisplayOrderReaction() throws Exception {
        ResponseUserTest userData = helper.loginSuperAdm();
        ReactionDTO reaction = this.helper.createReaction(userData);

        UpdateReactionDTO dto = new UpdateReactionDTO(
                null,
                null,
                null,
                11233L,
                null,
                null,
                null
        );

        MvcResult result = this.mockMvc.perform(patch(this.URL + "/" + reaction.id())
                        .content(this.objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<ReactionDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<ReactionDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isEqualTo(reaction.id());
        assertThat(response.data().name()).isEqualTo(reaction.name());
        assertThat(response.data().emojiUrl()).isEqualTo(reaction.emojiUrl());
        assertThat(response.data().emojiUnicode()).isEqualTo(reaction.emojiUnicode());
        assertThat(response.data().displayOrder()).isEqualTo(dto.displayOrder());
        assertThat(response.data().active()).isEqualTo(reaction.active());
        assertThat(response.data().visible()).isEqualTo(reaction.visible());
        assertThat(response.data().type()).isEqualTo(reaction.type());
    }

    @Test
    void shouldUpdateJustFieldActiveReaction() throws Exception {
        ResponseUserTest userData = helper.loginSuperAdm();
        ReactionDTO reaction = this.helper.createReaction(userData);

        UpdateReactionDTO dto = new UpdateReactionDTO(
                null,
                null,
                null,
                null,
                !reaction.active(),
                null,
                null
        );

        MvcResult result = this.mockMvc.perform(patch(this.URL + "/" + reaction.id())
                        .content(this.objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<ReactionDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<ReactionDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isEqualTo(reaction.id());
        assertThat(response.data().name()).isEqualTo(reaction.name());
        assertThat(response.data().emojiUrl()).isEqualTo(reaction.emojiUrl());
        assertThat(response.data().emojiUnicode()).isEqualTo(reaction.emojiUnicode());
        assertThat(response.data().displayOrder()).isEqualTo(reaction.displayOrder());
        assertThat(response.data().active()).isEqualTo(dto.active());
        assertThat(response.data().visible()).isEqualTo(reaction.visible());
        assertThat(response.data().type()).isEqualTo(reaction.type());
    }

    @Test
    void shouldUpdateJustFieldVisibleReaction() throws Exception {
        ResponseUserTest userData = helper.loginSuperAdm();
        ReactionDTO reaction = this.helper.createReaction(userData);

        UpdateReactionDTO dto = new UpdateReactionDTO(
                null,
                null,
                null,
                null,
                null,
                !reaction.visible(),
                null
        );

        MvcResult result = this.mockMvc.perform(patch(this.URL + "/" + reaction.id())
                        .content(this.objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<ReactionDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<ReactionDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isEqualTo(reaction.id());
        assertThat(response.data().name()).isEqualTo(reaction.name());
        assertThat(response.data().emojiUrl()).isEqualTo(reaction.emojiUrl());
        assertThat(response.data().emojiUnicode()).isEqualTo(reaction.emojiUnicode());
        assertThat(response.data().displayOrder()).isEqualTo(reaction.displayOrder());
        assertThat(response.data().active()).isEqualTo(reaction.active());
        assertThat(response.data().visible()).isEqualTo(dto.visible());
        assertThat(response.data().type()).isEqualTo(reaction.type());
    }

    @Test
    void shouldUpdateJustFieldTypeReaction() throws Exception {
        ResponseUserTest userData = helper.loginSuperAdm();
        ReactionDTO reaction = this.helper.createReaction(userData);

        UpdateReactionDTO dto = new UpdateReactionDTO(
                null,
                null,
                null,
                null,
                null,
                null,
                ReactionTypeEnum.AWARD
        );

        MvcResult result = this.mockMvc.perform(patch(this.URL + "/" + reaction.id())
                        .content(this.objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<ReactionDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<ReactionDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isEqualTo(reaction.id());
        assertThat(response.data().name()).isEqualTo(reaction.name());
        assertThat(response.data().emojiUrl()).isEqualTo(reaction.emojiUrl());
        assertThat(response.data().emojiUnicode()).isEqualTo(reaction.emojiUnicode());
        assertThat(response.data().displayOrder()).isEqualTo(reaction.displayOrder());
        assertThat(response.data().active()).isEqualTo(reaction.active());
        assertThat(response.data().visible()).isEqualTo(reaction.visible());
        assertThat(response.data().type()).isEqualTo(dto.type());
    }


}
