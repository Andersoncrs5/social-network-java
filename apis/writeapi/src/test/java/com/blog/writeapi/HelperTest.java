package com.blog.writeapi;

import cn.hutool.core.lang.UUID;
import com.blog.writeapi.dtos.user.CreateUserDTO;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.res.ResponseTokens;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class HelperTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    public ResponseTokens createUser() throws Exception {
        String key = UUID.fastUUID().toString();

        CreateUserDTO dto = new CreateUserDTO(
                "name" + key,
                "username" + key,
                "user" + key + "@gmail.com",
                "12345678"
        );

        MvcResult result = mockMvc.perform(post("/v1/auth/register")
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

        return response.data();
    }

}
