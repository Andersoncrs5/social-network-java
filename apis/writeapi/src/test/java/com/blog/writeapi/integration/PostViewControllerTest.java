package com.blog.writeapi.integration;

import com.blog.writeapi.configs.HelperTest;
import com.blog.writeapi.modules.post.dtos.PostDTO;
import com.blog.writeapi.modules.post.repository.PostRepository;
import com.blog.writeapi.modules.postView.repository.PostViewRepository;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.res.ResponseUserTest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PostViewControllerTest {
    private final String URL = "/v1/post-view";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostRepository repository;

    @Autowired
    private PostViewRepository postViewRepository;

    @Autowired
    private HelperTest helper;

    @BeforeEach
    void setup () {
        this.postViewRepository.deleteAll();
        this.repository.deleteAll();
    }

    @Test
    void shouldCreatePostView() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        PostDTO post = this.helper.createPost(userData);

        MvcResult result = this.mockMvc.perform(post(this.URL + "/" + post.id())
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isCreated()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.data()).isNull();
        assertThat(response.status()).isEqualTo(true);
    }

    @Test
    void shouldReturnNotFoundBecausePost() throws Exception {
        ResponseUserTest userData = this.helper.createUser();

        MvcResult result = this.mockMvc.perform(post(this.URL + "/" + 1998780203274176609L)
                        .header("Authorization", "Bearer " + userData.tokens().token())
                ).andExpect(status().isNotFound()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.data()).isNull();
        assertThat(response.status()).isEqualTo(false);
    }

}
