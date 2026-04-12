package com.blog.writeapi.integration.userBlock;

import com.blog.writeapi.configs.HelperTest;
import com.blog.writeapi.configs.TestContainerConfig;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestContainerConfig.class)
public class UserBlockControllerTest {
    private final String URL = "/v1/user-block";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository repository;

    @Autowired
    private HelperTest helper;

    @BeforeEach
    void setup () {
        this.repository.deleteAll();
    }

    @Test
    void shouldAddUserWithBlocked() throws Exception {
        ResponseUserTest userTest = this.helper.createUser();
        ResponseUserTest userTest2 = this.helper.createUser();

        MvcResult result = mockMvc.perform(post(this.URL + "/" + userTest2.userDTO().id() + "/toggle")
                .header("Authorization", "Bearer " + userTest.tokens().token())
        ).andExpect(status().isCreated()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldRemoveUserWithBlocked() throws Exception {
        ResponseUserTest userTest = this.helper.createUser();
        ResponseUserTest userTest2 = this.helper.createUser();

        this.helper.addUserWithBlock(userTest, userTest2);

        MvcResult result = mockMvc.perform(post(this.URL + "/" + userTest2.userDTO().id() + "/toggle")
                .header("Authorization", "Bearer " + userTest.tokens().token())
        ).andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldForbBecauseUserTriedBlockYourself() throws Exception {
        ResponseUserTest userTest = this.helper.createUser();

        MvcResult result = mockMvc.perform(post(this.URL + "/" + userTest.userDTO().id() + "/toggle")
                .header("Authorization", "Bearer " + userTest.tokens().token())
        ).andExpect(status().isForbidden()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturnNotFoundBecauseUserBlocker() throws Exception {
        ResponseUserTest userTest = this.helper.createUser();

        MvcResult result = mockMvc.perform(post(this.URL + "/" + (userTest.userDTO().id() + 1) + "/toggle")
                .header("Authorization", "Bearer " + userTest.tokens().token())
        ).andExpect(status().isNotFound()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);

        assertThat(response.data()).isNull();
    }

}
