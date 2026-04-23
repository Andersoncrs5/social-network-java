package com.blog.writeapi.integration;

import cn.hutool.core.lang.UUID;
import com.blog.writeapi.configs.HelperTest;
import com.blog.writeapi.configs.TestContainerConfig;
import com.blog.writeapi.modules.user.dtos.CreateUserDTO;
import com.blog.writeapi.modules.user.dtos.LoginUserDTO;
import com.blog.writeapi.modules.user.repository.UserRepository;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.res.ResponseTokens;
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestContainerConfig.class)
public class AuthControllerTest {

    private final String URL = "/v1/auth/";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private HelperTest helper;

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

    @Test
    void shouldUserLogInSystem() throws Exception {
        ResponseUserTest res = this.helper.createUser();

        LoginUserDTO dto = new LoginUserDTO(
                res.dto().email(),
                res.dto().password()
        );

        MvcResult result = mockMvc.perform(post(this.URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
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

    @Test
    void shouldFailUserLogInSystemBecauseEmailWrong() throws Exception {
        ResponseUserTest res = this.helper.createUser();

        LoginUserDTO dto = new LoginUserDTO(
                "user11111@gmail.com",
                res.dto().password()
        );

        mockMvc.perform(post(this.URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    void shouldFailUserLogInSystemBecausePasswordWrong() throws Exception {
        ResponseUserTest res = this.helper.createUser();

        LoginUserDTO dto = new LoginUserDTO(
                res.dto().email(),
                "12312312"
        );

        mockMvc.perform(post(this.URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

}
