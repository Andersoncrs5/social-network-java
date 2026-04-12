package com.blog.writeapi.integration.postShare;

import com.blog.writeapi.configs.HelperTest;
import com.blog.writeapi.configs.TestContainerConfig;
import com.blog.writeapi.modules.post.dtos.PostDTO;
import com.blog.writeapi.modules.postShare.dto.CreatePostShareDTO;
import com.blog.writeapi.modules.postShare.dto.PostShareDTO;
import com.blog.writeapi.modules.postShare.repository.PostShareRepository;
import com.blog.writeapi.utils.enums.postShare.SharePlatformEnum;
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
public class PostShareControllerTest {
    private final String URL = "/v1/post-share";

    @Autowired
    private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private PostShareRepository repository;

    @Autowired
    private HelperTest helper;

    @BeforeEach
    void setup () {
        this.repository.deleteAll();
    }

    @Test
    void shouldSharePost() throws Exception {
        ResponseUserTest userTest = this.helper.createUser();
        PostDTO post = this.helper.createPost(userTest);

        CreatePostShareDTO dto = new CreatePostShareDTO(
                post.id(),
                SharePlatformEnum.REDDIT
        );

        MvcResult result = mockMvc.perform(post(this.URL)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + userTest.tokens().token())
        ).andExpect(status().isCreated()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostShareDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostShareDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data()).isNotNull();
        assertThat(response.data().id()).isNotZero().isNotNegative();
        assertThat(response.data().post().id()).isEqualTo(post.id());
        assertThat(response.data().user().id()).isEqualTo(userTest.userDTO().id());
        assertThat(response.data().platform()).isEqualTo(dto.platform());
    }

    @Test
    void shouldReturnConflictBecausePostWasShared() throws Exception {
        ResponseUserTest userTest = this.helper.createUser();
        PostDTO post = this.helper.createPost(userTest);
        this.helper.sharePost(userTest, post);

        CreatePostShareDTO dto = new CreatePostShareDTO(
                post.id(),
                SharePlatformEnum.REDDIT
        );

        MvcResult result = mockMvc.perform(post(this.URL)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + userTest.tokens().token())
        ).andExpect(status().isConflict()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostShareDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostShareDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturnNotFoundWhenSharePost() throws Exception {
        ResponseUserTest userTest = this.helper.createUser();

        CreatePostShareDTO dto = new CreatePostShareDTO(
                userTest.userDTO().id(),
                SharePlatformEnum.REDDIT
        );

        MvcResult result = mockMvc.perform(post(this.URL)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + userTest.tokens().token())
        ).andExpect(status().isNotFound()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Void>> typeRef = new TypeReference<>() {};

        ResponseHttp<Void> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturnUnauthorizedWhenSharePost() throws Exception {
        ResponseUserTest userTest = this.helper.createUser();

        CreatePostShareDTO dto = new CreatePostShareDTO(
                userTest.userDTO().id(),
                SharePlatformEnum.REDDIT
        );

        mockMvc.perform(post(this.URL)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized()).andReturn();
    }

}
