package com.blog.writeapi.integration;

import com.blog.writeapi.HelperTest;
import com.blog.writeapi.dtos.category.CategoryDTO;
import com.blog.writeapi.dtos.post.PostDTO;
import com.blog.writeapi.dtos.postCategories.CreatePostCategoriesDTO;
import com.blog.writeapi.dtos.postCategories.PostCategoriesDTO;
import com.blog.writeapi.dtos.postCategories.UpdatePostCategoriesDTO;
import com.blog.writeapi.repositories.PostCategoriesRepository;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PostCategoriesControllerTest {
    private final String URL = "/v1/post-category";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired private PostCategoriesRepository repository;
    @Autowired private PostRepository postRepository;

    @Autowired
    private HelperTest helper;

    @BeforeEach
    void setup () {
        this.repository.deleteAll();
        this.postRepository.deleteAll();
    }

    // CREATE
    @Test
    void shouldReturnConflictBecauseCategoryAlreadyAdded() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        ResponseUserTest superAdmData = this.helper.loginSuperAdm();

        CategoryDTO categoryDTO = this.helper.createCategory(superAdmData, null);
        PostDTO postDTO = this.helper.createPost(userData);
        PostCategoriesDTO postCategoriesDTO = this.helper.addCategoryToPost(userData, categoryDTO, postDTO);

        CreatePostCategoriesDTO dto = new CreatePostCategoriesDTO(
                postDTO.id(),
                categoryDTO.id(),
                5,
                true,
                false
        );

        this.mockMvc.perform(post(this.URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isConflict())
                .andReturn();

    }

    @Test
    void shouldAddedCategoryToPost() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        ResponseUserTest superAdmData = this.helper.loginSuperAdm();

        CategoryDTO categoryDTO = this.helper.createCategory(superAdmData, null);
        PostDTO postDTO = this.helper.createPost(userData);

        CreatePostCategoriesDTO dto = new CreatePostCategoriesDTO(
                postDTO.id(),
                categoryDTO.id(),
                5,
                true,
                true
        );

        MvcResult result = this.mockMvc.perform(post(this.URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp< PostCategoriesDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostCategoriesDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().category().id()).isEqualTo(categoryDTO.id());
        assertThat(response.data().post().id()).isEqualTo(postDTO.id());
        assertThat(response.data().active()).isEqualTo(dto.active());
        assertThat(response.data().primary()).isEqualTo(dto.primary());
        assertThat(response.data().displayOrder()).isEqualTo(dto.displayOrder());
    }

    // DELETE
    @Test
    void shouldRemoveCategoryOfPost() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        ResponseUserTest superAdmData = this.helper.loginSuperAdm();

        CategoryDTO categoryDTO = this.helper.createCategory(superAdmData, null);
        PostDTO postDTO = this.helper.createPost(userData);
        PostCategoriesDTO postCategoriesDTO = this.helper.addCategoryToPost(userData, categoryDTO, postDTO);

        MvcResult result = this.mockMvc.perform(delete(this.URL + "/" + postCategoriesDTO.id())
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
    void shouldReturnForbWhenRemoveCategoryOfPost() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        ResponseUserTest userData2 = this.helper.createUser();
        ResponseUserTest superAdmData = this.helper.loginSuperAdm();

        CategoryDTO categoryDTO = this.helper.createCategory(superAdmData, null);
        PostDTO postDTO = this.helper.createPost(userData);
        PostCategoriesDTO postCategoriesDTO = this.helper.addCategoryToPost(userData, categoryDTO, postDTO);

        this.mockMvc.perform(delete(this.URL + "/" + postCategoriesDTO.id())
                        .header("Authorization", "Bearer " + userData2.tokens().token()
                        ))
                .andExpect(status().isForbidden())
                .andReturn();

    }

    // GET
    @Test
    void shouldReturnPostCategoryWhenGetById() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        ResponseUserTest superAdmData = this.helper.loginSuperAdm();

        CategoryDTO categoryDTO = this.helper.createCategory(superAdmData, null);
        PostDTO postDTO = this.helper.createPost(userData);
        PostCategoriesDTO postCategoriesDTO = this.helper.addCategoryToPost(userData, categoryDTO, postDTO);

        MvcResult result = this.mockMvc.perform(get(this.URL + "/" + postCategoriesDTO.id())
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostCategoriesDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostCategoriesDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isEqualTo(postCategoriesDTO.id());
        assertThat(response.data().displayOrder()).isEqualTo(postCategoriesDTO.displayOrder());
        assertThat(response.data().category().id()).isEqualTo(postCategoriesDTO.category().id());
        assertThat(response.data().post().id()).isEqualTo(postCategoriesDTO.post().id());
    }

    @Test
    void shouldReturnNullWhenGetById() throws Exception {
        ResponseUserTest userData = this.helper.createUser();

        MvcResult result = this.mockMvc.perform(get(this.URL + "/" + 111111111111111111L)
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);
    }

    // UPDATE
    @Test
    void shouldReturnPostCategoryUpdatedWithAllFieldsWhenExecEndpointUpdate() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        ResponseUserTest superAdmData = this.helper.loginSuperAdm();

        CategoryDTO categoryDTO = this.helper.createCategory(superAdmData, null);
        PostDTO postDTO = this.helper.createPost(userData);
        PostCategoriesDTO postCategoriesDTO = this.helper.addCategoryToPost(userData, categoryDTO, postDTO);

        UpdatePostCategoriesDTO dto = new UpdatePostCategoriesDTO(
                10,
                !postCategoriesDTO.primary(),
                !postCategoriesDTO.active()
        );

        MvcResult result = this.mockMvc.perform(patch(this.URL + "/" + postCategoriesDTO.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostCategoriesDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostCategoriesDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isEqualTo(postCategoriesDTO.id());
        assertThat(response.data().displayOrder()).isEqualTo(dto.displayOrder());
        assertThat(response.data().primary()).isEqualTo(dto.primary());
        assertThat(response.data().active()).isEqualTo(dto.active());

    }

    @Test
    void shouldReturnPostCategoryUpdatedJustFieldsDisplayWhenExecEndpointUpdate() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        ResponseUserTest superAdmData = this.helper.loginSuperAdm();

        CategoryDTO categoryDTO = this.helper.createCategory(superAdmData, null);
        PostDTO postDTO = this.helper.createPost(userData);
        PostCategoriesDTO postCategoriesDTO = this.helper.addCategoryToPost(userData, categoryDTO, postDTO);

        UpdatePostCategoriesDTO dto = new UpdatePostCategoriesDTO(
                10,
                null,
                null
        );

        MvcResult result = this.mockMvc.perform(patch(this.URL + "/" + postCategoriesDTO.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostCategoriesDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostCategoriesDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isEqualTo(postCategoriesDTO.id());
        assertThat(response.data().displayOrder()).isEqualTo(dto.displayOrder());
        assertThat(response.data().primary()).isEqualTo(postCategoriesDTO.primary());
        assertThat(response.data().active()).isEqualTo(postCategoriesDTO.active());
    }

    @Test
    void shouldReturnPostCategoryUpdatedJustFieldsTrueWhenExecEndpointUpdate() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        ResponseUserTest superAdmData = this.helper.loginSuperAdm();

        CategoryDTO categoryDTO = this.helper.createCategory(superAdmData, null);
        PostDTO postDTO = this.helper.createPost(userData);
        PostCategoriesDTO postCategoriesDTO = this.helper.addCategoryToPost(userData, categoryDTO, postDTO);

        UpdatePostCategoriesDTO dto = new UpdatePostCategoriesDTO(
                null,
                true,
                null
        );

        MvcResult result = this.mockMvc.perform(patch(this.URL + "/" + postCategoriesDTO.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostCategoriesDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostCategoriesDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isEqualTo(postCategoriesDTO.id());
        assertThat(response.data().displayOrder()).isEqualTo(postCategoriesDTO.displayOrder());
        assertThat(response.data().primary()).isEqualTo(dto.primary());
        assertThat(response.data().active()).isEqualTo(postCategoriesDTO.active());
    }

    @Test
    void shouldReturnPostCategoryUpdatedJustFieldsActiveWhenExecEndpointUpdate() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        ResponseUserTest superAdmData = this.helper.loginSuperAdm();

        CategoryDTO categoryDTO = this.helper.createCategory(superAdmData, null);
        PostDTO postDTO = this.helper.createPost(userData);
        PostCategoriesDTO postCategoriesDTO = this.helper.addCategoryToPost(userData, categoryDTO, postDTO);

        UpdatePostCategoriesDTO dto = new UpdatePostCategoriesDTO(
                null,
                null,
                true
        );

        MvcResult result = this.mockMvc.perform(patch(this.URL + "/" + postCategoriesDTO.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostCategoriesDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostCategoriesDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isEqualTo(postCategoriesDTO.id());
        assertThat(response.data().displayOrder()).isEqualTo(postCategoriesDTO.displayOrder());
        assertThat(response.data().primary()).isEqualTo(postCategoriesDTO.primary());
        assertThat(response.data().active()).isEqualTo(dto.active());
    }

    @Test
    void shouldReturnPostCategoryUpdatedNoFieldsWhenExecEndpointUpdate() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        ResponseUserTest superAdmData = this.helper.loginSuperAdm();

        CategoryDTO categoryDTO = this.helper.createCategory(superAdmData, null);
        PostDTO postDTO = this.helper.createPost(userData);
        PostCategoriesDTO postCategoriesDTO = this.helper.addCategoryToPost(userData, categoryDTO, postDTO);

        UpdatePostCategoriesDTO dto = new UpdatePostCategoriesDTO(
                null,
                null,
                null
        );

        MvcResult result = this.mockMvc.perform(patch(this.URL + "/" + postCategoriesDTO.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostCategoriesDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostCategoriesDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isEqualTo(postCategoriesDTO.id());
        assertThat(response.data().displayOrder()).isEqualTo(postCategoriesDTO.displayOrder());
        assertThat(response.data().primary()).isEqualTo(postCategoriesDTO.primary());
        assertThat(response.data().active()).isEqualTo(postCategoriesDTO.active());
    }

    @Test
    void shouldReturnForbWhenExecEndpointUpdate() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        ResponseUserTest userData2 = this.helper.createUser();
        ResponseUserTest superAdmData = this.helper.loginSuperAdm();

        CategoryDTO categoryDTO = this.helper.createCategory(superAdmData, null);
        PostDTO postDTO = this.helper.createPost(userData);
        PostCategoriesDTO postCategoriesDTO = this.helper.addCategoryToPost(userData, categoryDTO, postDTO);

        UpdatePostCategoriesDTO dto = new UpdatePostCategoriesDTO(
                10,
                !postCategoriesDTO.primary(),
                !postCategoriesDTO.active()
        );

        this.mockMvc.perform(patch(this.URL + "/" + postCategoriesDTO.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData2.tokens().token()
                        ))
                .andExpect(status().isForbidden())
                .andReturn();
    }


}
