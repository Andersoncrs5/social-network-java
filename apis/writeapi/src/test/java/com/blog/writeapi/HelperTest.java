package com.blog.writeapi;

import cn.hutool.core.lang.UUID;
import com.blog.writeapi.dtos.user.CreateUserDTO;
import com.blog.writeapi.dtos.user.UserDTO;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.res.ResponseTokens;
import com.blog.writeapi.utils.res.ResponseUserTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Service
@RequiredArgsConstructor
public class HelperTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public ResponseUserTest createUser() {
        try {
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
                    new TypeReference<>() {
                    };

            ResponseHttp<ResponseTokens> response =
                    objectMapper.readValue(registerJson, typeRef);

            assertThat(response.status()).isEqualTo(true);
            assertThat(response.message()).isNotBlank();
            assertThat(response.data().token()).isNotBlank();
            assertThat(response.data().refreshToken()).isNotBlank();

            MvcResult resultGet = mockMvc.perform(get("/v1/user/me")
                            .header("Authorization", "Bearer " + response.data().token()))
                    .andExpect(status().isOk()).andReturn();

            String json = resultGet.getResponse().getContentAsString();
            TypeReference<ResponseHttp<UserDTO>> typeRefGet = new TypeReference<>() {};

            ResponseHttp<UserDTO> responseGet =
                    objectMapper.readValue(json, typeRefGet);

            return new ResponseUserTest(
                    response.data(),
                    dto,
                    responseGet.data()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
