package com.blog.writeapi.integration;

import com.blog.writeapi.HelperTest;
import com.blog.writeapi.dtos.post.PostDTO;
import com.blog.writeapi.dtos.postFavorite.PostFavoriteDTO;
import com.blog.writeapi.repositories.PostFavoriteRepository;
import com.blog.writeapi.repositories.PostRepository;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.res.ResponseUserTest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PostFavoriteControllerTest {
    private final String URL = "/v1/post-favorite";

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @Autowired private PostFavoriteRepository postFavoriteRepository;
    @Autowired private PostRepository postRepository;

    @Autowired private HelperTest helper;

    @BeforeEach void setup () {
        this.postFavoriteRepository.deleteAll();
        this.postRepository.deleteAll();
    }

    // TOGGLE
    @Test
    void shouldAddPostWithFavorite() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        PostDTO post = this.helper.createPost(userData);

        MvcResult result = this.mockMvc.perform(post(this.URL + "/" + post.id())
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostFavoriteDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostFavoriteDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().post().id()).isEqualTo(post.id());
        assertThat(response.data().user().id()).isEqualTo(userData.userDTO().id());
    }

    @Test
    void shouldRemovePostWithFavorite() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        PostDTO post = this.helper.createPost(userData);
        this.helper.addPostWithFavorite(userData, post);

        MvcResult result = this.mockMvc.perform(post(this.URL + "/" + post.id())
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturnNotFoundPostWithFavorite() throws Exception {
        ResponseUserTest userData = this.helper.createUser();

        MvcResult result = this.mockMvc.perform(post(this.URL + "/" + 4675764357265767545L)
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);

        assertThat(response.data()).isNull();
    }



}
