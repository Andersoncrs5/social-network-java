package com.blog.writeapi.integration;

import com.blog.writeapi.HelperTest;
import com.blog.writeapi.dtos.comment.CommentDTO;
import com.blog.writeapi.dtos.commentAttachment.CommentAttachmentDTO;
import com.blog.writeapi.dtos.commentAttachment.CreateCommentAttachmentDTO;
import com.blog.writeapi.dtos.commentAttachment.UpdateCommentAttachmentDTO;
import com.blog.writeapi.dtos.post.PostDTO;
import com.blog.writeapi.repositories.CommentAttachmentRepository;
import com.blog.writeapi.repositories.CommentRepository;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CommentAttachmentControllerTest {
    private final String URL = "/v1/comment-attachment";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired private CommentAttachmentRepository repository;
    @Autowired private CommentRepository commentRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private PostAttachmentRepository postAttachmentRepository;

    @Autowired
    private HelperTest helper;

    @BeforeEach
    void setup () {
        this.repository.deleteAll();
        this.postAttachmentRepository.deleteAll();
        this.commentRepository.deleteAll();
        this.postRepository.deleteAll();
    }

    @Test
    void shouldCreateNewFile() throws Exception {
        ResponseUserTest user = this.helper.createUser();
        PostDTO postDTO = this.helper.createPost(user);
        CommentDTO commentDTO = this.helper.createComment(user, postDTO, null);

        this.upload(user, commentDTO);
    }

    @Test
    void shouldDeleteFileSuccess() throws Exception {
        ResponseUserTest user = this.helper.createUser();
        PostDTO postDTO = this.helper.createPost(user);
        CommentDTO commentDTO = this.helper.createComment(user, postDTO, null);

        CommentAttachmentDTO attachmentDTO = this.upload(user, commentDTO);

        this.mockMvc.perform(delete(this.URL + "/" + attachmentDTO.getId())
                        .header("Authorization", "Bearer " + user.tokens().token())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(true)))
                .andExpect(jsonPath("$.message", is("Attachment deleted")))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void shouldDeleteFileFailBecauseResourceOwnerMismatchException() throws Exception {
        ResponseUserTest user = this.helper.createUser();
        ResponseUserTest user2 = this.helper.createUser();

        PostDTO postDTO = this.helper.createPost(user);
        CommentDTO commentDTO = this.helper.createComment(user, postDTO, null);

        CommentAttachmentDTO attachmentDTO = this.upload(user, commentDTO);

        this.mockMvc.perform(delete(this.URL + "/" + attachmentDTO.getId())
                        .header("Authorization", "Bearer " + user2.tokens().token())
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status", is(false)))
                .andExpect(jsonPath("$.message", is("You don't have permission to delete this attachment.")))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void shouldDeleteFileFailReturnNotFound() throws Exception {
        ResponseUserTest user = this.helper.createUser();
        PostDTO postDTO = this.helper.createPost(user);
        CommentDTO commentDTO = this.helper.createComment(user, postDTO, null);

        CommentAttachmentDTO attachmentDTO = this.upload(user, commentDTO);

        this.mockMvc.perform(delete(this.URL + "/" + (attachmentDTO.getId() + 1))
                        .header("Authorization", "Bearer " + user.tokens().token())
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(false)))
                .andExpect(jsonPath("$.message", is("Attachment not found")))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void shouldUpdateAttachmentAllFields() throws Exception {
        ResponseUserTest user = this.helper.createUser();
        PostDTO postDTO = this.helper.createPost(user);
        CommentDTO commentDTO = this.helper.createComment(user, postDTO, null);

        CommentAttachmentDTO attachmentDTO = this.upload(user, commentDTO);

        UpdateCommentAttachmentDTO dto = new UpdateCommentAttachmentDTO(
                "name updated",
                !attachmentDTO.getIsPublic(),
                !attachmentDTO.getIsVisible()
        );


        MvcResult result = this.mockMvc.perform(patch(this.URL + "/" + attachmentDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + user.tokens().token())
                )
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        ResponseHttp<CommentAttachmentDTO> response = objectMapper.readValue(json, new TypeReference<>() {});

        assertThat(response.data().getFileName()).isEqualTo(dto.fileName());
        assertThat(response.data().getIsPublic()).isEqualTo(dto.isPublic());
        assertThat(response.data().getIsVisible()).isEqualTo(dto.isVisible());

        assertThat(response.data().getComment().id()).isEqualTo(commentDTO.id());
        assertThat(response.data().getUploader().id()).isEqualTo(user.userDTO().id());
        assertThat(response.data().getStorageKey()).isEqualTo(attachmentDTO.getStorageKey());
        assertThat(response.data().getContentType()).isEqualTo(attachmentDTO.getContentType());
        assertThat(response.data().getFileSize()).isEqualTo(attachmentDTO.getFileSize());

        assertThat(response.status()).isTrue();

    }

    @Test
    void shouldReturnForbWhenUpdateAttachmentBecauseAnotherUser() throws Exception {
        ResponseUserTest user = this.helper.createUser();
        ResponseUserTest user3 = this.helper.createUser();

        PostDTO postDTO = this.helper.createPost(user);
        CommentDTO commentDTO = this.helper.createComment(user, postDTO, null);

        CommentAttachmentDTO attachmentDTO = this.upload(user, commentDTO);

        UpdateCommentAttachmentDTO dto = new UpdateCommentAttachmentDTO(
                "name updated",
                !attachmentDTO.getIsPublic(),
                !attachmentDTO.getIsVisible()
        );

        this.mockMvc.perform(patch(this.URL + "/" + attachmentDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + user3.tokens().token())
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnNotFoundWhenUpdateAttachment() throws Exception {
        ResponseUserTest user = this.helper.createUser();
        PostDTO postDTO = this.helper.createPost(user);
        CommentDTO commentDTO = this.helper.createComment(user, postDTO, null);

        CommentAttachmentDTO attachmentDTO = this.upload(user, commentDTO);

        UpdateCommentAttachmentDTO dto = new UpdateCommentAttachmentDTO(
                "name updated",
                !attachmentDTO.getIsPublic(),
                !attachmentDTO.getIsVisible()
        );

        this.mockMvc.perform(patch(this.URL + "/" + (attachmentDTO.getId() + 1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + user.tokens().token())
                )
                .andExpect(status().isNotFound());
    }

    private CommentAttachmentDTO upload(ResponseUserTest userData, CommentDTO commentDTO) throws Exception {
        Path path = Paths.get("src/test/java/com/blog/writeapi/utils/resources/foto.png");
        byte[] content = Files.readAllBytes(path);

        MockMultipartFile filePart = new MockMultipartFile(
                "file",
                "foto.png",
                "image/png",
                content
        );

        MvcResult result = this.mockMvc.perform(multipart(this.URL)
                        .file(filePart)
                        .param("commentId", commentDTO.id().toString())
                        .param("fileName", "pochita-wallpaper")
                        .param("contentType", "image/png")
                        .param("isPublic", "true")
                        .param("isVisible", "true")
                        .header("Authorization", "Bearer " + userData.tokens().token()))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        ResponseHttp<CommentAttachmentDTO> response = objectMapper.readValue(json, new TypeReference<>() {});

        assertThat(response.data().getFileName()).isEqualTo("pochita-wallpaper");
        assertThat(response.data().getComment().id()).isEqualTo(commentDTO.id());
        assertThat(response.data().getUploader().id()).isEqualTo(userData.userDTO().id());
        assertThat(response.data().getStorageKey()).isNotBlank();
        assertThat(response.data().getContentType()).isEqualTo("image/png");
        assertThat(response.data().getFileSize()).isEqualTo(filePart.getBytes().length);

        assertThat(response.status()).isTrue();

        return response.data();
    }

}
