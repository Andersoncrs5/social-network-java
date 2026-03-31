package com.blog.writeapi.integration.commentView;

import com.blog.writeapi.HelperTest;
import com.blog.writeapi.modules.comment.dtos.CommentDTO;
import com.blog.writeapi.modules.comment.repository.CommentRepository;
import com.blog.writeapi.modules.commentView.repository.CommentViewRepository;
import com.blog.writeapi.modules.post.dtos.PostDTO;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CommentViewControllerTest {
    private final String URL = "/v1/comment-view";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private PostRepository postRepository;
    @Autowired private CommentRepository commentRepository;
    @Autowired private CommentViewRepository repository;

    @Autowired
    private HelperTest helper;

    @BeforeEach
    void setup () {
        this.repository.deleteAll();
        this.commentRepository.deleteAll();
        this.postRepository.deleteAll();
    }

    @Test
    void shouldCreatePostView() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        ResponseUserTest userData2 = this.helper.createUser();

        PostDTO post = this.helper.createPost(userData);
        CommentDTO comment = this.helper.createComment(userData, post, null);

        MvcResult result = this.mockMvc.perform(post(this.URL + "/" + comment.id())
                        .header("Authorization", "Bearer " + userData2.tokens().token()
                        ))
                .andExpect(status().isCreated()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Void>> typeRef = new TypeReference<>() {};

        ResponseHttp<Void> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.data()).isNull();
        assertThat(response.status()).isEqualTo(true);
    }

    @Test
    void shouldNotFoundBecauseComment() throws Exception {
        ResponseUserTest userData2 = this.helper.createUser();

        MvcResult result = this.mockMvc.perform(post(this.URL + "/" + 1998780203274176609L)
                        .header("Authorization", "Bearer " + userData2.tokens().token())
        ).andExpect(status().isNotFound()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Void>> typeRef = new TypeReference<>() {};

        ResponseHttp<Void> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.data()).isNull();
        assertThat(response.status()).isEqualTo(false);
    }

}
