package com.blog.writeapi.integration;

import cn.hutool.core.lang.UUID;
import com.blog.writeapi.configs.HelperTest;
import com.blog.writeapi.configs.TestContainerConfig;
import com.blog.writeapi.modules.tag.dtos.TagDTO;
import com.blog.writeapi.modules.tag.repository.TagRepository;
import com.blog.writeapi.modules.userTagPreference.dtos.UserTagPreferenceDTO;
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
import org.springframework.http.MediaType;
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
public class UserTagPreferenceControllerTest {
    private final String URL = "/v1/user-tag-preference";

    @Autowired
    private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private HelperTest helper;

    @Autowired private TagRepository tagRepository;

    @BeforeEach
    void setup() {
        this.tagRepository.deleteAll();
    }

    @Test
    void shouldCreatePreferenceIntoUser() throws Exception {
        var traceId = UUID.randomUUID().toString();
        ResponseUserTest userData = this.helper.createUser();

        ResponseUserTest master = helper.loginSuperAdm();
        TagDTO tag = this.helper.createTag(master);

        MvcResult result = this.mockMvc.perform(post(this.URL + "/" + tag.id() + "/toggle")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + userData.tokens().token())
                .header("X-Idempotency-Key", traceId)
        ).andExpect(status().isCreated()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<UserTagPreferenceDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<UserTagPreferenceDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.traceId()).isNotBlank().isEqualTo(traceId);
        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.data().id()).isNotZero().isPositive().isNotNull();
        assertThat(response.data().tag().id()).isEqualTo(tag.id());
        assertThat(response.data().user().id()).isEqualTo(userData.userDTO().id());
    }

    @Test
    void shouldRemovePreferenceIntoUser() throws Exception {
        var traceId = UUID.randomUUID().toString();
        ResponseUserTest userData = this.helper.createUser();

        ResponseUserTest master = helper.loginSuperAdm();
        TagDTO tag = this.helper.createTag(master);
        this.helper.addTagInPreferenceUser(userData, tag);

        MvcResult result = this.mockMvc.perform(post(this.URL + "/" + tag.id() + "/toggle")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + userData.tokens().token())
                .header("X-Idempotency-Key", traceId)
        ).andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank().isEqualTo(traceId);
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturnNotFoundWhenCreatePreferenceIntoUser() throws Exception {
        var traceId = UUID.randomUUID().toString();
        ResponseUserTest userData = this.helper.createUser();

        ResponseUserTest master = helper.loginSuperAdm();
        TagDTO tag = this.helper.createTag(master);

        MvcResult result = this.mockMvc.perform(post(this.URL + "/" + (tag.id() + 1) + "/toggle")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + userData.tokens().token())
                .header("X-Idempotency-Key", traceId)
        ).andExpect(status().isNotFound()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.traceId()).isNotBlank();
        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);
        assertThat(response.data()).isNull();
    }

}
