package com.blog.writeapi.integration;

import com.blog.writeapi.HelperTest;
import com.blog.writeapi.dtos.category.CategoryDTO;
import com.blog.writeapi.dtos.post.PostDTO;
import com.blog.writeapi.dtos.postAttachment.CreatePostAttachmentDTO;
import com.blog.writeapi.dtos.postAttachment.PostAttachmentDTO;
import com.blog.writeapi.dtos.postCategories.PostCategoriesDTO;
import com.blog.writeapi.repositories.PostAttachmentRepository;
import com.blog.writeapi.repositories.PostCategoriesRepository;
import com.blog.writeapi.repositories.PostRepository;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.res.ResponseUserTest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PostAttachmentControllerTest {
    private final String URL = "/v1/post-attachment";


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired private PostAttachmentRepository repository;
    @Autowired private PostRepository postRepository;

    @Autowired
    private HelperTest helper;

    @BeforeEach
    void setup () {
        this.repository.deleteAll();
        this.postRepository.deleteAll();
    }

    @Test
    void shouldCreateNewFile() throws Exception {
        // 1. Setup de dados (Usuário e Post)
        ResponseUserTest userData = this.helper.createUser();
        PostDTO postDTO = this.helper.createPost(userData);

        MockMultipartFile filePart = new MockMultipartFile(
                "file",
                "pochita.png",
                "image/png",
                "conteudo-binario".getBytes()
        );

        MvcResult result = this.mockMvc.perform(multipart(this.URL)
                        .file(filePart)
                        .param("postId", postDTO.id().toString())
                        .param("fileName", "pochita-wallpaper")
                        .param("contentType", "image/png")
                        .param("isPublic", "true")
                        .param("isVisible", "true")
                        .header("Authorization", "Bearer " + userData.tokens().token()))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        ResponseHttp<PostAttachmentDTO> response = objectMapper.readValue(json, new TypeReference<>() {});

        assertThat(response.data().getFileName()).isEqualTo("pochita-wallpaper");
        assertThat(response.data().getPost().id()).isEqualTo(postDTO.id());
    }

}
