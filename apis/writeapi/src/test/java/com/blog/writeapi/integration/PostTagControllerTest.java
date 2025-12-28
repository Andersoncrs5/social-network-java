package com.blog.writeapi.integration;

import com.blog.writeapi.HelperTest;
import com.blog.writeapi.dtos.post.PostDTO;
import com.blog.writeapi.dtos.postTag.CreatePostTagDTO;
import com.blog.writeapi.dtos.postTag.PostTagDTO;
import com.blog.writeapi.dtos.postTag.UpdatePostTagDTO;
import com.blog.writeapi.dtos.tag.TagDTO;
import com.blog.writeapi.repositories.PostRepository;
import com.blog.writeapi.repositories.TagRepository;
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
public class PostTagControllerTest {
    private final String URL = "/v1/post-tag";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private PostRepository repository;
    @Autowired private TagRepository tagRepository;

    @Autowired private HelperTest helper;

    @BeforeEach
    void setup () {
        this.repository.deleteAll();
        this.tagRepository.deleteAll();
    }

    // CREATE
    @Test
    void shouldAddedTagToPost() throws Exception {
        ResponseUserTest superAdm = this.helper.loginSuperAdm();
        ResponseUserTest userTest = this.helper.createUser();

        TagDTO tagDTO = this.helper.createTag(superAdm);
        PostDTO postDTO = this.helper.createPost(userTest);

        CreatePostTagDTO dto = new CreatePostTagDTO(
                postDTO.id(),
                tagDTO.id(),
                true,
                true
        );

        MvcResult result = this.mockMvc.perform(post(this.URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userTest.tokens().token()
                        ))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostTagDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostTagDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.data().id()).isNotNegative();
        assertThat(response.data().post().id()).isEqualTo(postDTO.id());
        assertThat(response.data().tag().id()).isEqualTo(tagDTO.id());
        assertThat(response.data().active()).isEqualTo(dto.active());
        assertThat(response.data().visible()).isEqualTo(dto.visible());
    }

    @Test
    void shouldReturnForbAddedTagToPostBecauseAnotherUser() throws Exception {
        ResponseUserTest superAdm = this.helper.loginSuperAdm();

        ResponseUserTest userTest = this.helper.createUser();
        ResponseUserTest userTest2 = this.helper.createUser();

        TagDTO tagDTO = this.helper.createTag(superAdm);
        PostDTO postDTO = this.helper.createPost(userTest);

        CreatePostTagDTO dto = new CreatePostTagDTO(
                postDTO.id(),
                tagDTO.id(),
                true,
                true
        );

        MvcResult result = this.mockMvc.perform(post(this.URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userTest2.tokens().token()
                        ))
                .andExpect(status().isForbidden())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);
        assertThat(response.data()).isNull();
    }

    // DELETE
    @Test
    void shouldRemoveTagOfPost() throws Exception {
        ResponseUserTest superAdm = this.helper.loginSuperAdm();
        ResponseUserTest userTest = this.helper.createUser();

        TagDTO tagDTO = this.helper.createTag(superAdm);
        PostDTO postDTO = this.helper.createPost(userTest);
        PostTagDTO postTagDTO = this.helper.addTagToPost(userTest, postDTO, tagDTO);

        MvcResult result = this.mockMvc.perform(delete(this.URL + "/" + postTagDTO.id())
                        .header("Authorization", "Bearer " + userTest.tokens().token()))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturnForbTheRemoveTagOfPostBecauseUserNotOwer() throws Exception {
        ResponseUserTest superAdm = this.helper.loginSuperAdm();
        ResponseUserTest userTest = this.helper.createUser();
        ResponseUserTest userTest2 = this.helper.createUser();

        TagDTO tagDTO = this.helper.createTag(superAdm);
        PostDTO postDTO = this.helper.createPost(userTest);
        PostTagDTO postTagDTO = this.helper.addTagToPost(userTest, postDTO, tagDTO);

        this.mockMvc.perform(delete(this.URL + "/" + postTagDTO.id())
                        .header("Authorization", "Bearer " + userTest2.tokens().token()))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    // UPDATE
    @Test
    void shouldUpdateAllFieldsPostTag() throws Exception {
        ResponseUserTest superAdm = this.helper.loginSuperAdm();
        ResponseUserTest userTest = this.helper.createUser();

        TagDTO tagDTO = this.helper.createTag(superAdm);
        PostDTO postDTO = this.helper.createPost(userTest);
        PostTagDTO postTagDTO = this.helper.addTagToPost(userTest, postDTO, tagDTO);

        UpdatePostTagDTO dto = new UpdatePostTagDTO(
                !postTagDTO.active(),
                !postTagDTO.visible()
        );

        MvcResult result = this.mockMvc.perform(patch(this.URL + "/" + postTagDTO.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userTest.tokens().token()
                        ))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostTagDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostTagDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isEqualTo(postTagDTO.id());
        assertThat(response.data().active()).isEqualTo(dto.active());
        assertThat(response.data().visible()).isEqualTo(dto.visible());
        assertThat(response.data().post().id()).isEqualTo(postDTO.id());
        assertThat(response.data().tag().id()).isEqualTo(tagDTO.id());

    }

    @Test
    void shouldUpdateJustFieldActivePostTag() throws Exception {
        ResponseUserTest superAdm = this.helper.loginSuperAdm();
        ResponseUserTest userTest = this.helper.createUser();

        TagDTO tagDTO = this.helper.createTag(superAdm);
        PostDTO postDTO = this.helper.createPost(userTest);
        PostTagDTO postTagDTO = this.helper.addTagToPost(userTest, postDTO, tagDTO);

        UpdatePostTagDTO dto = new UpdatePostTagDTO(
                !postTagDTO.active(),
                null
        );

        MvcResult result = this.mockMvc.perform(patch(this.URL + "/" + postTagDTO.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userTest.tokens().token()
                        ))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostTagDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostTagDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isEqualTo(postTagDTO.id());
        assertThat(response.data().active()).isEqualTo(dto.active());
        assertThat(response.data().visible()).isEqualTo(postTagDTO.visible());
        assertThat(response.data().post().id()).isEqualTo(postDTO.id());
        assertThat(response.data().tag().id()).isEqualTo(tagDTO.id());

    }

    @Test
    void shouldUpdateJustFieldVisiblePostTag() throws Exception {
        ResponseUserTest superAdm = this.helper.loginSuperAdm();
        ResponseUserTest userTest = this.helper.createUser();

        TagDTO tagDTO = this.helper.createTag(superAdm);
        PostDTO postDTO = this.helper.createPost(userTest);
        PostTagDTO postTagDTO = this.helper.addTagToPost(userTest, postDTO, tagDTO);

        UpdatePostTagDTO dto = new UpdatePostTagDTO(
                null,
                !postTagDTO.visible()
        );

        MvcResult result = this.mockMvc.perform(patch(this.URL + "/" + postTagDTO.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userTest.tokens().token()
                        ))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostTagDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostTagDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isEqualTo(postTagDTO.id());
        assertThat(response.data().active()).isEqualTo(postTagDTO.active());
        assertThat(response.data().visible()).isEqualTo(dto.visible());
        assertThat(response.data().post().id()).isEqualTo(postDTO.id());
        assertThat(response.data().tag().id()).isEqualTo(tagDTO.id());

    }

    @Test
    void shouldReturnForbWhenUpdatePostTag() throws Exception {
        ResponseUserTest superAdm = this.helper.loginSuperAdm();

        ResponseUserTest userTest = this.helper.createUser();
        ResponseUserTest userTest2 = this.helper.createUser();

        TagDTO tagDTO = this.helper.createTag(superAdm);
        PostDTO postDTO = this.helper.createPost(userTest);
        PostTagDTO postTagDTO = this.helper.addTagToPost(userTest, postDTO, tagDTO);

        UpdatePostTagDTO dto = new UpdatePostTagDTO(
                null,
                !postTagDTO.visible()
        );

        this.mockMvc.perform(patch(this.URL + "/" + postTagDTO.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userTest2.tokens().token()
                        ))
                .andExpect(status().isForbidden());

    }


}
