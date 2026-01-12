package com.blog.writeapi.integration;

import com.blog.writeapi.HelperTest;
import com.blog.writeapi.dtos.post.PostDTO;
import com.blog.writeapi.dtos.postVote.PostVoteDTO;
import com.blog.writeapi.dtos.postVote.TogglePostVoteDTO;
import com.blog.writeapi.models.enums.votes.VoteTypeEnum;
import com.blog.writeapi.repositories.PostRepository;
import com.blog.writeapi.repositories.PostVoteRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PostVoteControllerTest {
    private final String URL = "/v1/post-vote";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostVoteRepository repository;
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private HelperTest helper;

    @BeforeEach
    void setup () {
        this.repository.deleteAll();
        this.postRepository.deleteAll();
    }

    @Test
    void shouldAddedVoteUpInPost() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        PostDTO post = this.helper.createPost(userData);

        TogglePostVoteDTO dto = new TogglePostVoteDTO(
                post.id(),
                VoteTypeEnum.UPVOTE
        );

        MvcResult result = this.mockMvc.perform(post(this.URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostVoteDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostVoteDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.data().id()).isNotZero().isNotNegative();
        assertThat(response.data().post().id()).isEqualTo(post.id());
        assertThat(response.data().user().id()).isEqualTo(userData.userDTO().id());
        assertThat(response.data().type()).isEqualTo(dto.type());
    }

    @Test
    void shouldAddedVoteDownInPost() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        PostDTO post = this.helper.createPost(userData);

        TogglePostVoteDTO dto = new TogglePostVoteDTO(
                post.id(),
                VoteTypeEnum.DOWNVOTE
        );

        MvcResult result = this.mockMvc.perform(post(this.URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostVoteDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostVoteDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.data().id()).isNotZero().isNotNegative();
        assertThat(response.data().post().id()).isEqualTo(post.id());
        assertThat(response.data().user().id()).isEqualTo(userData.userDTO().id());
        assertThat(response.data().type()).isEqualTo(dto.type());
    }

    @Test
    void shouldDeleteVote() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        PostDTO post = this.helper.createPost(userData);
        PostVoteDTO vote = this.helper.addedPostVote(userData, post, VoteTypeEnum.UPVOTE);

        TogglePostVoteDTO dto = new TogglePostVoteDTO(
                post.id(),
                vote.type()
        );

        MvcResult result = this.mockMvc.perform(post(this.URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.data()).isNull();
    }

    @Test
    void shouldChangeTypeOfVote() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        PostDTO post = this.helper.createPost(userData);
        PostVoteDTO vote = this.helper.addedPostVote(userData, post, VoteTypeEnum.UPVOTE);

        TogglePostVoteDTO dto = new TogglePostVoteDTO(
                post.id(),
                VoteTypeEnum.DOWNVOTE
        );

        MvcResult result = this.mockMvc.perform(post(this.URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostVoteDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostVoteDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.data().id()).isNotZero().isNotNegative();
        assertThat(response.data().post().id()).isEqualTo(post.id());
        assertThat(response.data().user().id()).isEqualTo(userData.userDTO().id());
        assertThat(response.data().type()).isEqualTo(dto.type());
        assertThat(response.data().type()).isNotEqualTo(vote.type());
    }


}
