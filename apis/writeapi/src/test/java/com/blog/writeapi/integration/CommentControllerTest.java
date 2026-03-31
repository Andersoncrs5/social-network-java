package com.blog.writeapi.integration;

import com.blog.writeapi.configs.HelperTest;
import com.blog.writeapi.modules.comment.dtos.CommentDTO;
import com.blog.writeapi.modules.comment.dtos.CreateCommentDTO;
import com.blog.writeapi.modules.comment.dtos.UpdateCommentDTO;
import com.blog.writeapi.modules.post.dtos.PostDTO;
import com.blog.writeapi.modules.comment.repository.CommentRepository;
import com.blog.writeapi.modules.postCategory.repository.PostCategoriesRepository;
import com.blog.writeapi.modules.post.repository.PostRepository;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CommentControllerTest {

    private final String URL = "/v1/comment";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private HelperTest helper;

    @Autowired private CommentRepository repository;
    @Autowired private PostCategoriesRepository postCategoriesRepository;
    @Autowired private PostRepository postRepository;

    @BeforeEach
    void setup () {
        this.repository.deleteAll();
        this.postCategoriesRepository.deleteAll();
        this.postRepository.deleteAll();
    }

    // DELETE
    @Test
    void shouldDeleteComment() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        PostDTO post = this.helper.createPost(userData);
        CommentDTO comment = this.helper.createComment(userData, post, null);

        MvcResult result = this.mockMvc.perform(delete(this.URL + "/" + comment.id())
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
    void shouldReturnForbDeleteComment() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        ResponseUserTest userData2 = this.helper.createUser();

        PostDTO post = this.helper.createPost(userData);
        CommentDTO comment = this.helper.createComment(userData, post, null);

        this.mockMvc.perform(delete(this.URL + "/" + comment.id())
                        .header("Authorization", "Bearer " + userData2.tokens().token()
                        ))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    void shouldReturnNotFoundDeleteComment() throws Exception {
        ResponseUserTest userData = this.helper.createUser();

        MvcResult result = this.mockMvc.perform(delete(this.URL + "/" + 456374657396476397L)
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);

        assertThat(response.data()).isNull();
    }

    // CREATE
    @Test
    void shouldCreateComment() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        PostDTO post = this.helper.createPost(userData);

        CreateCommentDTO dto = new CreateCommentDTO(
                "content",
                post.id(),
                null
        );

        MvcResult result = this.mockMvc.perform(post(this.URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<CommentDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<CommentDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isNotNegative().isNotZero();
        assertThat(response.data().parentId()).isNull();
        assertThat(response.data().content()).isEqualTo(dto.content());
        assertThat(response.data().post().id()).isEqualTo(post.id());
        assertThat(response.data().user().id()).isEqualTo(userData.userDTO().id());
    }

    @Test
    void shouldCreateCommentOnComment() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        PostDTO post = this.helper.createPost(userData);
        CommentDTO comment = this.helper.createComment(userData, post, null);

        CreateCommentDTO dto = new CreateCommentDTO(
                "content",
                post.id(),
                comment.id()
        );

        MvcResult result = this.mockMvc.perform(post(this.URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<CommentDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<CommentDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isNotNegative().isNotZero();
        assertThat(response.data().parentId()).isEqualTo(comment.id());
        assertThat(response.data().content()).isEqualTo(dto.content());
        assertThat(response.data().post().id()).isEqualTo(post.id());
        assertThat(response.data().user().id()).isEqualTo(userData.userDTO().id());
    }

    //UPDATE
    @Test
    void shouldUpdateCommentAllField() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        PostDTO post = this.helper.createPost(userData);
        CommentDTO comment = this.helper.createComment(userData, post, null);

        UpdateCommentDTO dto = new UpdateCommentDTO(
                """
                        NewContentNewContentNewContentNewContentNewContentNewContentNewContent
                        NewContentNewContentNewContentNewContentNewContentNewContentNewContent
                        NewContentNewContentNewContentNewContentNewContentNewContentNewContent
                        NewContentNewContentNewContentNewContentNewContentNewContentNewContent
                        NewContentNewContentNewContentNewContentNewContentNewContentNewContent
                        NewContentNewContentNewContentNewContentNewContentNewContentNewContent
                        NewContentNewContentNewContentNewContentNewContentNewContentNewContent
                        NewContentNewContentNewContentNewContentNewContentNewContentNewContent
                        """
        );

        MvcResult result = this.mockMvc.perform(patch(this.URL + "/" + comment.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Bearer " + userData.tokens().token())
        ).andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<CommentDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<CommentDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isEqualTo(comment.id());
        assertThat(response.data().content()).isEqualTo(dto.content());
        assertThat(response.data().parentId()).isEqualTo(comment.parentId());

    }

    @Test
    void shouldUpdateCommentNoField() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        PostDTO post = this.helper.createPost(userData);
        CommentDTO comment = this.helper.createComment(userData, post, null);

        UpdateCommentDTO dto = new UpdateCommentDTO(
                null
        );

        MvcResult result = this.mockMvc.perform(patch(this.URL + "/" + comment.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Bearer " + userData.tokens().token())
        ).andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<CommentDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<CommentDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isEqualTo(comment.id());
        assertThat(response.data().content()).isEqualTo(comment.content());
        assertThat(response.data().parentId()).isEqualTo(comment.parentId());

    }

    @Test
    void shouldReturnNotFoundUpdateComment() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        PostDTO post = this.helper.createPost(userData);
        CommentDTO comment = this.helper.createComment(userData, post, null);

        UpdateCommentDTO dto = new UpdateCommentDTO(
                null
        );

        MvcResult result = this.mockMvc.perform(patch(this.URL + "/" + (comment.id()+1))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Bearer " + userData.tokens().token())
        ).andExpect(status().isNotFound()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);

    }

}
