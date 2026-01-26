package com.blog.writeapi.integration;

import com.blog.writeapi.HelperTest;
import com.blog.writeapi.dtos.commentFavorite.CommentFavoriteDTO;
import com.blog.writeapi.dtos.post.PostDTO;
import com.blog.writeapi.dtos.postAttachment.PostAttachmentDTO;
import com.blog.writeapi.repositories.PostAttachmentRepository;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

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
        assertThat(response.data().getUploader().id()).isEqualTo(userData.userDTO().id());
        assertThat(response.data().getStorageKey()).isNotBlank();
        assertThat(response.data().getContentType()).isEqualTo("image/png");
        assertThat(response.data().getFileSize()).isEqualTo(filePart.getBytes().length);

        assertThat(response.status()).isTrue();
    }

    @Test
    void shouldReturnNotFoundCreateNewFile() throws Exception {
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
                        .param("postId", String.valueOf(postDTO.id() + 1))
                        .param("fileName", "pochita-wallpaper")
                        .param("contentType", "image/png")
                        .param("isPublic", "true")
                        .param("isVisible", "true")
                        .header("Authorization", "Bearer " + userData.tokens().token()))
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        ResponseHttp<Object> response = objectMapper.readValue(json, new TypeReference<>() {});

        assertThat(response.data()).isNull();
        assertThat(response.status()).isFalse();
    }

    @Test
    void shouldDeleteAtt() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        PostDTO postDTO = this.helper.createPost(userData);

        PostAttachmentDTO attachmentDTO = this.upload(userData, postDTO);

        this.mockMvc.perform(delete(this.URL + "/" + attachmentDTO.getId())
                .header("Authorization", "Bearer " + userData.tokens().token())
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(true)))
                .andExpect(jsonPath("$.message", is("Attachment deleted")))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void shouldReturnNotFoundDeleteAtt() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        PostDTO postDTO = this.helper.createPost(userData);

        PostAttachmentDTO attachmentDTO = this.upload(userData, postDTO);

        this.mockMvc.perform(delete(this.URL + "/" + attachmentDTO.getId())
                        .header("Authorization", "Bearer " + userData.tokens().token())
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(false)))
                .andExpect(jsonPath("$.message", is("Attachment not found")))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void shouldReturn401DeleteAtt() throws Exception{
        ResponseUserTest userData = this.helper.createUser();
        ResponseUserTest userData1 = this.helper.createUser();
        PostDTO postDTO = this.helper.createPost(userData);

        PostAttachmentDTO attachmentDTO = this.upload(userData, postDTO);

        MvcResult result = this.mockMvc.perform(delete(this.URL + "/" + attachmentDTO.getId())
                .header("Authorization", "Bearer " + userData1.tokens().token())
        ).andExpect(status().isForbidden()).andReturn();

        String json = result.getResponse().getContentAsString();
        ResponseHttp<Object> response = objectMapper.readValue(json, new TypeReference<>() {});

        assertThat(response.data()).isNull();
        assertThat(response.status()).isFalse();
    }

    private PostAttachmentDTO upload(ResponseUserTest userData, PostDTO postDTO) throws Exception {
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
        assertThat(response.data().getUploader().id()).isEqualTo(userData.userDTO().id());
        assertThat(response.data().getStorageKey()).isNotBlank();
        assertThat(response.data().getContentType()).isEqualTo("image/png");
        assertThat(response.data().getFileSize()).isEqualTo(filePart.getBytes().length);

        assertThat(response.status()).isTrue();

        return response.data();
    }

}
