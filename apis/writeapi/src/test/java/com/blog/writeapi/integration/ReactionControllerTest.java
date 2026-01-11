package com.blog.writeapi.integration;

import com.blog.writeapi.HelperTest;
import com.blog.writeapi.dtos.reaction.CreateReactionDTO;
import com.blog.writeapi.dtos.reaction.ReactionDTO;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

}
