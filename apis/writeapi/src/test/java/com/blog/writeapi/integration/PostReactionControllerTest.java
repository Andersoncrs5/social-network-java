package com.blog.writeapi.integration;

import com.blog.writeapi.HelperTest;
import com.blog.writeapi.dtos.post.PostDTO;
import com.blog.writeapi.dtos.postReaction.CreatePostReactionDTO;
import com.blog.writeapi.dtos.postReaction.PostReactionDTO;
import com.blog.writeapi.dtos.reaction.ReactionDTO;
import com.blog.writeapi.repositories.PostReactionRepository;
import com.blog.writeapi.repositories.PostRepository;
import com.blog.writeapi.repositories.ReactionRepository;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.res.ResponseUserTest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PostReactionControllerTest {
    private final String URL = "/v1/post-reaction";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private HelperTest helper;

    @Autowired private PostRepository postRepository;
    @Autowired private PostReactionRepository repository;
    @Autowired private ReactionRepository reactionRepository;

    @BeforeEach
    void setup() {
        this.repository.deleteAll();
        this.postRepository.deleteAll();
        this.reactionRepository.deleteAll();
    }

    @Test
    void shouldCreateNewReactionToPost() throws Exception {
        ResponseUserTest superAdm = this.helper.loginSuperAdm();
        ResponseUserTest userData = this.helper.createUser();
        ResponseUserTest userData2 = this.helper.createUser();

        PostDTO post = this.helper.createPost(userData);
        ReactionDTO reactionDTO = this.helper.createReaction(superAdm);

        CreatePostReactionDTO dto = new CreatePostReactionDTO(
                post.id(),
                reactionDTO.id()
        );

        MvcResult result = mockMvc.perform(post(this.URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData2.tokens().token())
                )
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostReactionDTO>> typeRef = new TypeReference<>() {};
        ResponseHttp<PostReactionDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isNotNegative().isNotZero();
        assertThat(response.data().post().id()).isEqualTo(post.id());
        assertThat(response.data().user().id()).isEqualTo(userData2.userDTO().id());
        assertThat(response.data().reaction().id()).isEqualTo(reactionDTO.id());

    }

    @Test
    void shouldDeleteNewReactionToPost() throws Exception {
        ResponseUserTest superAdm = this.helper.loginSuperAdm();
        ResponseUserTest userData = this.helper.createUser();

        ResponseUserTest userData2 = this.helper.createUser();

        PostDTO post = this.helper.createPost(userData);
        ReactionDTO reactionDTO = this.helper.createReaction(superAdm);
        PostReactionDTO postReactionDTO = this.helper.toggleReactionToPost(userData2, post, reactionDTO);

        CreatePostReactionDTO dto = new CreatePostReactionDTO(
                post.id(),
                reactionDTO.id()
        );

        MvcResult result = mockMvc.perform(post(this.URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData2.tokens().token())
                )
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostReactionDTO>> typeRef = new TypeReference<>() {};
        ResponseHttp<PostReactionDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data()).isNull();

    }

    @Test
    void shouldUpdateNewReactionToPost() throws Exception {
        ResponseUserTest superAdm = this.helper.loginSuperAdm();
        ResponseUserTest userData = this.helper.createUser();

        ResponseUserTest userData2 = this.helper.createUser();

        PostDTO post = this.helper.createPost(userData);
        ReactionDTO reactionDTO = this.helper.createReaction(superAdm);
        ReactionDTO reactionDTO2 = this.helper.createReaction(superAdm);

        PostReactionDTO postReactionDTO = this.helper.toggleReactionToPost(userData2, post, reactionDTO);

        CreatePostReactionDTO dto = new CreatePostReactionDTO(
                post.id(),
                reactionDTO2.id()
        );

        MvcResult result = mockMvc.perform(post(this.URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData2.tokens().token())
                )
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostReactionDTO>> typeRef = new TypeReference<>() {};
        ResponseHttp<PostReactionDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isEqualTo(postReactionDTO.id());
        assertThat(response.data().post().id()).isEqualTo(post.id());
        assertThat(response.data().reaction().id()).isEqualTo(reactionDTO2.id());
        assertThat(response.data().user().id()).isEqualTo(userData2.userDTO().id());

    }

}
