package com.blog.writeapi.integration.userView;

import com.blog.writeapi.configs.HelperTest;
import com.blog.writeapi.configs.TestContainerConfig;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.res.ResponseUserTest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestContainerConfig.class)
public class UserViewControllerTest {
    private final String URL = "/v1/user-view";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private HelperTest helper;

    @Test
    void shouldCreateView() throws Exception {
        var traceId = UUID.randomUUID().toString();
        ResponseUserTest userViewer = helper.createUser();
        ResponseUserTest userViewed = helper.createUser();

        MvcResult result = mockMvc.perform(post(this.URL + "/" + userViewed.userDTO().id())
                        .header("Authorization", "Bearer " + userViewer.tokens().token())
                        .header("X-Idempotency-Key", traceId)
                ).andExpect(status().isCreated()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Void>> typeRef = new TypeReference<>() {};

        ResponseHttp<Void> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.traceId()).isEqualTo(traceId);
        assertThat(response.message()).isNotBlank().containsIgnoringCase("User view created");
        assertThat(response.status()).isTrue();
    }

    @Test
    void shouldReturnBadRequestCreateView() throws Exception {
        var traceId = UUID.randomUUID().toString();
        ResponseUserTest userViewer = helper.createUser();

        MvcResult result = mockMvc.perform(post(this.URL + "/" + userViewer.userDTO().id())
                        .header("Authorization", "Bearer " + userViewer.tokens().token())
                        .header("X-Idempotency-Key", traceId)
                ).andExpect(status().isBadRequest()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Void>> typeRef = new TypeReference<>() {};

        ResponseHttp<Void> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.traceId()).isNotBlank();
        assertThat(response.message()).isNotBlank().containsIgnoringCase("You cannot record a view on your own profile.");
        assertThat(response.status()).isFalse();
    }

    @Test
    void shouldFailBecauseIdempotencyKeyMissedCreateView() throws Exception {
        ResponseUserTest userViewer = helper.createUser();
        ResponseUserTest userViewed = helper.createUser();

        MvcResult result = mockMvc.perform(post(this.URL + "/" + userViewed.userDTO().id())
                        .header("Authorization", "Bearer " + userViewer.tokens().token())
                ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    void shouldReturn200BecauseViewAlreadyExistsCreateView() throws Exception {
        var traceId = UUID.randomUUID().toString();
        ResponseUserTest userViewer = helper.createUser();
        ResponseUserTest userViewed = helper.createUser();

        this.helper.createUserView(userViewer, userViewed);

        MvcResult result = mockMvc.perform(post(this.URL + "/" + userViewed.userDTO().id())
                        .header("Authorization", "Bearer " + userViewer.tokens().token())
                        .header("X-Idempotency-Key", traceId)
                ).andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Void>> typeRef = new TypeReference<>() {};

        ResponseHttp<Void> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.traceId()).isEqualTo(traceId);
        assertThat(response.message()).isNotBlank().containsIgnoringCase("already exists");
        assertThat(response.status()).isTrue();
    }

    @Test
    void shouldFailBecauseUserViewedNotFoundCreateView() throws Exception {
        var traceId = UUID.randomUUID().toString();
        ResponseUserTest userViewer = helper.createUser();

        MvcResult result = mockMvc.perform(post(this.URL + "/" + (userViewer.userDTO().id() + 1111))
                        .header("Authorization", "Bearer " + userViewer.tokens().token())
                        .header("X-Idempotency-Key", traceId)
                ).andExpect(status().isNotFound()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Void>> typeRef = new TypeReference<>() {};

        ResponseHttp<Void> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.traceId()).isNotBlank();
        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isFalse();
    }

}
