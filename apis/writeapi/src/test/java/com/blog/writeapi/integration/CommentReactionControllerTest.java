package com.blog.writeapi.integration;

import com.blog.writeapi.HelperTest;
import com.blog.writeapi.dtos.comment.CommentDTO;
import com.blog.writeapi.dtos.commentReaction.CommentReactionDTO;
import com.blog.writeapi.dtos.commentReaction.CreateCommentReactionDTO;
import com.blog.writeapi.dtos.post.PostDTO;
import com.blog.writeapi.dtos.postReaction.PostReactionDTO;
import com.blog.writeapi.dtos.reaction.ReactionDTO;
import com.blog.writeapi.repositories.*;
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
public class CommentReactionControllerTest {
    private final String URL = "/v1/comment-reaction";

    @Autowired
    private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private HelperTest helper;

    @Autowired private PostRepository postRepository;
    @Autowired private CommentRepository commentRepository;
    @Autowired private CommentReactionRepository repository;
    @Autowired private ReactionRepository reactionRepository;

    @BeforeEach
    void setup() {
        this.repository.deleteAll();
        this.commentRepository.deleteAll();
        this.postRepository.deleteAll();
        this.reactionRepository.deleteAll();
    }

    @Test
    void shouldCreateNewReactionToComment() throws Exception {
        ResponseUserTest superAdm = this.helper.loginSuperAdm();

        ResponseUserTest userData = this.helper.createUser();
        ResponseUserTest userData2 = this.helper.createUser();

        PostDTO post = this.helper.createPost(userData);
        CommentDTO comment = this.helper.createComment(userData, post, null);
        ReactionDTO reactionDTO = this.helper.createReaction(superAdm);

        CreateCommentReactionDTO dto = new CreateCommentReactionDTO(
                comment.id(),
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
        TypeReference<ResponseHttp<CommentReactionDTO>> typeRef = new TypeReference<>() {};
        ResponseHttp<CommentReactionDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().comment().id()).isEqualTo(comment.id());
        assertThat(response.data().reaction().id()).isEqualTo(reactionDTO.id());
        assertThat(response.data().user().id()).isEqualTo(userData2.userDTO().id());
    }

    @Test
    void shouldDeleteReactionToComment() throws Exception {
        ResponseUserTest superAdm = this.helper.loginSuperAdm();

        ResponseUserTest userData = this.helper.createUser();
        ResponseUserTest userData2 = this.helper.createUser();

        PostDTO post = this.helper.createPost(userData);
        CommentDTO comment = this.helper.createComment(userData, post, null);
        ReactionDTO reactionDTO = this.helper.createReaction(superAdm);
        this.helper.toggleReactionToComment(userData2, comment, reactionDTO);

        CreateCommentReactionDTO dto = new CreateCommentReactionDTO(
                comment.id(),
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
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};
        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldUpdateReactionInComment() throws Exception {
        ResponseUserTest superAdm = this.helper.loginSuperAdm();

        ResponseUserTest userData = this.helper.createUser();
        ResponseUserTest userData2 = this.helper.createUser();

        PostDTO post = this.helper.createPost(userData);
        CommentDTO comment = this.helper.createComment(userData, post, null);
        ReactionDTO reactionDTO = this.helper.createReaction(superAdm);
        ReactionDTO reactionDTO2 = this.helper.createReaction(superAdm);

        this.helper.toggleReactionToComment(userData2, comment, reactionDTO);

        CreateCommentReactionDTO dto = new CreateCommentReactionDTO(
                comment.id(),
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
        TypeReference<ResponseHttp<CommentReactionDTO>> typeRef = new TypeReference<>() {};
        ResponseHttp<CommentReactionDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().comment().id()).isEqualTo(comment.id());
        assertThat(response.data().reaction().id()).isEqualTo(reactionDTO2.id());
        assertThat(response.data().user().id()).isEqualTo(userData2.userDTO().id());
    }


}
