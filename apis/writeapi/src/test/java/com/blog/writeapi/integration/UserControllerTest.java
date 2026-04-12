package com.blog.writeapi.integration;

import com.blog.writeapi.configs.HelperTest;
import com.blog.writeapi.configs.TestContainerConfig;
import com.blog.writeapi.modules.user.dtos.UpdateUserDTO;
import com.blog.writeapi.modules.user.dtos.UserDTO;
import com.blog.writeapi.modules.user.repository.UserRepository;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.res.ResponseUserTest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestContainerConfig.class)
public class UserControllerTest {
    private final String URL = "/v1/user";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HelperTest helper;

    @BeforeEach
    void setup() {
        this.userRepository.deleteAll();
    }

    @Test
    void shouldGetUser() throws Exception {
        ResponseUserTest userData = helper.createUser();

        MvcResult result = mockMvc.perform(get(this.URL + "/me")
                        .header("Authorization", "Bearer " + userData.tokens().token()))
                .andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<UserDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<UserDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.data().email()).isEqualTo(userData.dto().email());
    }

    @Test
    void shouldDeleteUser() throws Exception {
        ResponseUserTest userData = helper.createUser();

        MvcResult result = mockMvc.perform(delete(this.URL)
                        .header("Authorization", "Bearer " + userData.tokens().token()))
                .andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
    }

    @Test
    void shouldUpdateUser() throws Exception {
        ResponseUserTest userData = helper.createUser();

        UpdateUserDTO dto = new UpdateUserDTO(
                "Name updated",
                "username12345",
                "0123456789"
        );

        MvcResult result = mockMvc.perform(patch(this.URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<UserDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<UserDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.data().email()).isEqualTo(userData.userDTO().email());
        assertThat(response.data().name()).isEqualTo(dto.name());
        assertThat(response.data().username()).isEqualTo(dto.username());
    }

    @Test
    void shouldUpdateUserJustName() throws Exception {
        ResponseUserTest userData = helper.createUser();

        UpdateUserDTO dto = new UpdateUserDTO(
                "Name updated",
                null,
                null
        );

        MvcResult result = mockMvc.perform(patch(this.URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        System.out.println(json);
        TypeReference<ResponseHttp<UserDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<UserDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.data().email()).isEqualTo(userData.userDTO().email());
        assertThat(response.data().name()).isEqualTo(dto.name());
        assertThat(response.data().username()).isEqualTo(userData.userDTO().username());
    }

    @Test
    void shouldUpdateUserJustUsername() throws Exception {
        ResponseUserTest userData = helper.createUser();

        UpdateUserDTO dto = new UpdateUserDTO(
                null,
                "username12345678",
                null
        );

        MvcResult result = mockMvc.perform(patch(this.URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        System.out.println(json);
        TypeReference<ResponseHttp<UserDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<UserDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.data().email()).isEqualTo(userData.userDTO().email());
        assertThat(response.data().name()).isEqualTo(userData.userDTO().name());
        assertThat(response.data().username()).isEqualTo(dto.username());
    }

    @Test
    void shouldUpdateUserJustPassword() throws Exception {
        ResponseUserTest userData = helper.createUser();

        UpdateUserDTO dto = new UpdateUserDTO(
                null,
                null,
                "0123456789"
        );

        MvcResult result = mockMvc.perform(patch(this.URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        System.out.println(json);
        TypeReference<ResponseHttp<UserDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<UserDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.data().email()).isEqualTo(userData.userDTO().email());
        assertThat(response.data().name()).isEqualTo(userData.userDTO().name());
        assertThat(response.data().username()).isEqualTo(userData.userDTO().username());
    }


}
