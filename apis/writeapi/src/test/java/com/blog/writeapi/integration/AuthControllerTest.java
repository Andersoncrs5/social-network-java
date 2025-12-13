package com.blog.writeapi.integration;

import cn.hutool.core.lang.UUID;
import com.blog.writeapi.dtos.user.CreateUserDTO;
import com.blog.writeapi.repositories.UserRepository;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.res.ResponseTokens;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    private final String URL = "/v1/auth/";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        this.userRepository.deleteAll();
    }

    @Test
    void shouldCreateNewUser() throws Exception {
        String key = UUID.fastUUID().toString();

        CreateUserDTO dto = new CreateUserDTO(
                "name" + key,
                "username" + key,
                "user" + key + "@gmail.com",
                "12345678"
        );

        MvcResult result = mockMvc.perform(post(URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<ResponseTokens>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<ResponseTokens> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(true);
        assertThat(response.message()).isNotBlank();
        assertThat(response.data().token()).isNotBlank();
        assertThat(response.data().refreshToken()).isNotBlank();
    }



}
