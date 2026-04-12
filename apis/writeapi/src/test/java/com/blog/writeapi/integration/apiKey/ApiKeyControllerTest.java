package com.blog.writeapi.integration.apiKey;

import com.blog.writeapi.configs.HelperTest;
import com.blog.writeapi.configs.TestContainerConfig;
import com.blog.writeapi.modules.apiKeys.dto.CreateApiKeyDTO;
import com.blog.writeapi.modules.apiKeys.repository.ApiKeyRepository;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.res.ResponseUserTest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import(TestContainerConfig.class)
public class ApiKeyControllerTest {
    private final String URL = "/v1/admin/api-keys";

    @Autowired
    private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ApiKeyRepository repository;

    @Autowired
    private HelperTest helper;

    @Test
    void shouldCreateApiKey() throws Exception {
        ResponseUserTest adm = this.helper.loginSuperAdm();

        CreateApiKeyDTO dto = new CreateApiKeyDTO("api-read");

        MvcResult result = mockMvc.perform(post(this.URL)
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adm.tokens().token())
        ).andExpect(status().isCreated()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<String>> typeRef = new TypeReference<>() {};

        ResponseHttp<String> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.version()).isEqualTo(1);

        assertThat(response.data()).isNotBlank();
    }

    @Test
    @DisplayName("Should return 401 when API Key is invalid")
    void shouldReturn401WhenApiKeyIsInvalid() throws Exception {
        ResponseUserTest adm = this.helper.loginSuperAdm();
        String apiKey = this.helper.createApiKey(adm);

        mockMvc.perform(get(this.URL + "/internal/test-auth")
                .header("X-API-KEY", apiKey)
        ).andExpect(status().isOk()).andReturn();
    }

    @Test
    @DisplayName("Should return 401 when API Key is missing")
    void shouldReturn401WhenApiKeyIsMissing() throws Exception {
        mockMvc.perform(get(this.URL + "/internal/test-auth")
                .header("X-API-KEY", "any")
        ).andExpect(status().isUnauthorized()).andReturn();
    }

}
