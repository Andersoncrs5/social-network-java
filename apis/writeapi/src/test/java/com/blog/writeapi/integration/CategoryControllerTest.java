package com.blog.writeapi.integration;

import com.blog.writeapi.HelperTest;
import com.blog.writeapi.dtos.category.CategoryDTO;
import com.blog.writeapi.dtos.category.CreateCategoryDTO;
import com.blog.writeapi.dtos.category.UpdateCategoryDTO;
import com.blog.writeapi.repositories.CategoryRepository;
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

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CategoryControllerTest {
    private final String URL = "/v1/category";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository repository;

    @Autowired
    private HelperTest helper;

    @BeforeEach
    void setup() {
        this.repository.deleteAll();
    }

    // CREATE ENDPOINT
    @Test
    void shouldCreateCategoryOnCategory() throws Exception {
        ResponseUserTest userData = helper.loginSuperAdm();
        CategoryDTO category = this.helper.createCategory(userData, null);
        CreateCategoryDTO dto = getCreateCategoryDTO(category.id());

        MvcResult result = this.mockMvc.perform(post(this.URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<CategoryDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<CategoryDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.data().name()).isEqualTo(dto.name());
        assertThat(response.data().description()).isEqualTo(dto.description());
        assertThat(response.data().slug()).isEqualTo(dto.slug());
        assertThat(response.data().isActive()).isEqualTo(dto.isActive());
        assertThat(response.data().visible()).isEqualTo(dto.visible());
        assertThat(response.data().displayOrder()).isEqualTo(dto.displayOrder());

    }

    @Test
    void shouldCreateCategory() throws Exception {
        ResponseUserTest userData = helper.loginSuperAdm();
        Random random = new Random();

        char letter = (char) ('a' + random.nextInt(26));
        char letter2 = (char) ('a' + random.nextInt(26));
        char letter3 = (char) ('a' + random.nextInt(26));

        CreateCategoryDTO dto = new CreateCategoryDTO(
            "software engineer " + letter + letter2 + letter3,
                "",
                "software-engineer-" + letter + letter2 + letter3,
                true,
                true,
                0,
                null
        );

        MvcResult result = this.mockMvc.perform(post(this.URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<CategoryDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<CategoryDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.data().name()).isEqualTo(dto.name());
        assertThat(response.data().description()).isEqualTo(dto.description());
        assertThat(response.data().slug()).isEqualTo(dto.slug());
        assertThat(response.data().isActive()).isEqualTo(dto.isActive());
        assertThat(response.data().visible()).isEqualTo(dto.visible());
        assertThat(response.data().displayOrder()).isEqualTo(dto.displayOrder());

    }

    @Test
    void shouldReturnForbiddenWhenCreateCategory() throws Exception {
        ResponseUserTest userData = helper.createUser();
        Random random = new Random();

        char letter = (char) ('a' + random.nextInt(26));
        char letter2 = (char) ('a' + random.nextInt(26));
        char letter3 = (char) ('a' + random.nextInt(26));

        CreateCategoryDTO dto = new CreateCategoryDTO(
            "software engineer " + letter + letter2 + letter3,
                "",
                "software-engineer",
                true,
                true,
                0,
                null
        );

        this.mockMvc.perform(post(this.URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isForbidden());

    }

    @Test
    void shouldReturnNotFoundWhenTryCreateCategoryOnCategory() throws Exception {
        ResponseUserTest userData = helper.loginSuperAdm();
        CreateCategoryDTO dto = getCreateCategoryDTO(3354353565425627256L);

        MvcResult result = this.mockMvc.perform(post(this.URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);
        assertThat(response.data()).isNull();
    }

    // GET ENDPOINT
    @Test
    void shouldGetCategory() throws Exception {
        ResponseUserTest userData = helper.loginSuperAdm();
        CategoryDTO category = this.helper.createCategory(userData, null);
        CreateCategoryDTO dto = getCreateCategoryDTO(category.id());

        MvcResult result = this.mockMvc.perform(get(this.URL+"/" + category.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<CategoryDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<CategoryDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.data().id()).isEqualTo(category.id());
        assertThat(response.data().name()).isEqualTo(category.name());
        assertThat(response.data().description()).isEqualTo(category.description());
        assertThat(response.data().slug()).isEqualTo(category.slug());
        assertThat(response.data().isActive()).isEqualTo(category.isActive());
        assertThat(response.data().visible()).isEqualTo(category.visible());
        assertThat(response.data().displayOrder()).isEqualTo(category.displayOrder());
    }

    @Test
    void shouldReturnNullGetCategory() throws Exception {
        ResponseUserTest userData = helper.loginSuperAdm();
        CategoryDTO category = this.helper.createCategory(userData, null);
        CreateCategoryDTO dto = getCreateCategoryDTO(category.id());

        MvcResult result = this.mockMvc.perform(get(this.URL+"/" + (category.id() + 1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);
        assertThat(response.data()).isNull();

    }

    @Test
    void shouldReturnBadRequestGetCategoryWhenPassZero() throws Exception {
        ResponseUserTest userData = helper.loginSuperAdm();
        CategoryDTO category = this.helper.createCategory(userData, null);
        CreateCategoryDTO dto = getCreateCategoryDTO(category.id());

        MvcResult result = this.mockMvc.perform(get(this.URL+"/" + 0)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);
        assertThat(response.data()).isNull();

    }

    @Test
    void shouldReturnBadRequestGetCategoryWhenPassNumberNegative() throws Exception {
        ResponseUserTest userData = helper.loginSuperAdm();
        CategoryDTO category = this.helper.createCategory(userData, null);
        CreateCategoryDTO dto = getCreateCategoryDTO(category.id());

        MvcResult result = this.mockMvc.perform(get(this.URL+"/" + -1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);
        assertThat(response.data()).isNull();

    }

    // DELETE ENDPOINT
    @Test
    void shouldDeleteCategory() throws Exception {
        ResponseUserTest userData = helper.loginSuperAdm();
        CategoryDTO category = this.helper.createCategory(userData, null);
        CreateCategoryDTO dto = getCreateCategoryDTO(category.id());

        MvcResult result = this.mockMvc.perform(delete(this.URL+"/" + category.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
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
    void shouldReturnNullDeleteCategory() throws Exception {
        ResponseUserTest userData = helper.loginSuperAdm();
        CategoryDTO category = this.helper.createCategory(userData, null);
        CreateCategoryDTO dto = getCreateCategoryDTO(category.id());

        MvcResult result = this.mockMvc.perform(delete(this.URL+"/" + (category.id() + 1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);
        assertThat(response.data()).isNull();

    }

    @Test
    void shouldReturnBadRequestDeleteCategoryWhenPassZero() throws Exception {
        ResponseUserTest userData = helper.loginSuperAdm();
        CategoryDTO category = this.helper.createCategory(userData, null);
        CreateCategoryDTO dto = getCreateCategoryDTO(category.id());

        MvcResult result = this.mockMvc.perform(delete(this.URL+"/" + 0)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);
        assertThat(response.data()).isNull();

    }

    @Test
    void shouldReturnBadRequestDeleteCategoryWhenPassNumberNegative() throws Exception {
        ResponseUserTest userData = helper.loginSuperAdm();
        CategoryDTO category = this.helper.createCategory(userData, null);
        CreateCategoryDTO dto = getCreateCategoryDTO(category.id());

        MvcResult result = this.mockMvc.perform(delete(this.URL+"/" + -1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);
        assertThat(response.data()).isNull();

    }

    // UPDATE ENDPOINT
    @Test
    void shouldUpdateCategoryAllFields() throws Exception {
        ResponseUserTest userData = helper.loginSuperAdm();
        CategoryDTO category = this.helper.createCategory(userData, null);

        UpdateCategoryDTO dto = new UpdateCategoryDTO(
                category.id(),
                category.name() + " update",
                category.description() + " update",
                category.slug() + "-update",
                !category.isActive(),
                !category.visible(),
                1,
                null,
                null
        );

        MvcResult result = this.mockMvc.perform(patch(this.URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<CategoryDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<CategoryDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.data().id()).isEqualTo(category.id());
        assertThat(response.data().name()).isEqualTo(dto.name());
        assertThat(response.data().description()).isEqualTo(dto.description());
        assertThat(response.data().slug()).isEqualTo(dto.slug());
        assertThat(response.data().isActive()).isEqualTo(dto.isActive());
        assertThat(response.data().visible()).isEqualTo(dto.visible());
        assertThat(response.data().displayOrder()).isEqualTo(dto.displayOrder());
    } // error

    @Test
    void shouldUpdateCategoryNoFields() throws Exception {
        ResponseUserTest userData = helper.loginSuperAdm();
        CategoryDTO category = this.helper.createCategory(userData, null);

        UpdateCategoryDTO dto = new UpdateCategoryDTO(
                category.id(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        MvcResult result = this.mockMvc.perform(patch(this.URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<CategoryDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<CategoryDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.data().id()).isEqualTo(category.id());
        assertThat(response.data().name()).isEqualTo(category.name());
        assertThat(response.data().description()).isEqualTo(category.description());
        assertThat(response.data().slug()).isEqualTo(category.slug());
        assertThat(response.data().isActive()).isEqualTo(category.isActive());
        assertThat(response.data().visible()).isEqualTo(category.visible());
        assertThat(response.data().displayOrder()).isEqualTo(category.displayOrder());
    }

    @Test
    void shouldUpdateCategoryButUpdateJustName() throws Exception {
        ResponseUserTest userData = helper.loginSuperAdm();
        CategoryDTO category = this.helper.createCategory(userData, null);

        UpdateCategoryDTO dto = new UpdateCategoryDTO(
                category.id(),
                category.name() + " update",
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        MvcResult result = this.mockMvc.perform(patch(this.URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<CategoryDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<CategoryDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.data().id()).isEqualTo(category.id());
        assertThat(response.data().name()).isEqualTo(dto.name());
        assertThat(response.data().description()).isEqualTo(category.description());
        assertThat(response.data().slug()).isEqualTo(category.slug());
        assertThat(response.data().isActive()).isEqualTo(category.isActive());
        assertThat(response.data().visible()).isEqualTo(category.visible());
        assertThat(response.data().displayOrder()).isEqualTo(category.displayOrder());
        assertThat(response.data().createdAt().getSecond()).isEqualTo(category.createdAt().getSecond());
    }

    @Test
    void shouldUpdateCategoryButUpdateJustDescription() throws Exception {
        ResponseUserTest userData = helper.loginSuperAdm();
        CategoryDTO category = this.helper.createCategory(userData, null);

        UpdateCategoryDTO dto = new UpdateCategoryDTO(
                category.id(),
                null,
                "desc update",
                null,
                null,
                null,
                null,
                null,
                null
        );

        MvcResult result = this.mockMvc.perform(patch(this.URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<CategoryDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<CategoryDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.data().id()).isEqualTo(category.id());
        assertThat(response.data().name()).isEqualTo(category.name());
        assertThat(response.data().description()).isEqualTo(dto.description());
        assertThat(response.data().slug()).isEqualTo(category.slug());
        assertThat(response.data().isActive()).isEqualTo(category.isActive());
        assertThat(response.data().visible()).isEqualTo(category.visible());
        assertThat(response.data().displayOrder()).isEqualTo(category.displayOrder());
        assertThat(response.data().createdAt().getSecond()).isEqualTo(category.createdAt().getSecond());
    }

    @Test
    void shouldUpdateCategoryButUpdateJustSlug() throws Exception {
        ResponseUserTest userData = helper.loginSuperAdm();
        CategoryDTO category = this.helper.createCategory(userData, null);

        UpdateCategoryDTO dto = new UpdateCategoryDTO(
                category.id(),
                null,
                null,
                "abd-xyz",
                null,
                null,
                null,
                null,
                null
        );

        MvcResult result = this.mockMvc.perform(patch(this.URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<CategoryDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<CategoryDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.data().id()).isEqualTo(category.id());
        assertThat(response.data().name()).isEqualTo(category.name());
        assertThat(response.data().description()).isEqualTo(category.description());
        assertThat(response.data().slug()).isEqualTo(dto.slug());
        assertThat(response.data().isActive()).isEqualTo(category.isActive());
        assertThat(response.data().visible()).isEqualTo(category.visible());
        assertThat(response.data().displayOrder()).isEqualTo(category.displayOrder());
        assertThat(response.data().createdAt().getSecond()).isEqualTo(category.createdAt().getSecond());
    }

    @Test
    void shouldUpdateCategoryButUpdateJustIsActive() throws Exception {
        ResponseUserTest userData = helper.loginSuperAdm();
        CategoryDTO category = this.helper.createCategory(userData, null);

        UpdateCategoryDTO dto = new UpdateCategoryDTO(
                category.id(),
                null,
                null,
                null,
                !category.isActive(),
                null,
                null,
                null,
                null
        );

        MvcResult result = this.mockMvc.perform(patch(this.URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<CategoryDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<CategoryDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.data().id()).isEqualTo(category.id());
        assertThat(response.data().name()).isEqualTo(category.name());
        assertThat(response.data().description()).isEqualTo(category.description());
        assertThat(response.data().slug()).isEqualTo(category.slug());
        assertThat(response.data().isActive()).isEqualTo(dto.isActive());
        assertThat(response.data().visible()).isEqualTo(category.visible());
        assertThat(response.data().displayOrder()).isEqualTo(category.displayOrder());
        assertThat(response.data().createdAt().getSecond()).isEqualTo(category.createdAt().getSecond());
    }

    @Test
    void shouldUpdateCategoryButUpdateJustVisible() throws Exception {
        ResponseUserTest userData = helper.loginSuperAdm();
        CategoryDTO category = this.helper.createCategory(userData, null);

        UpdateCategoryDTO dto = new UpdateCategoryDTO(
                category.id(),
                null,
                null,
                null,
                null,
                !category.visible(),
                null,
                null,
                null
        );

        MvcResult result = this.mockMvc.perform(patch(this.URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<CategoryDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<CategoryDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.data().id()).isEqualTo(category.id());
        assertThat(response.data().name()).isEqualTo(category.name());
        assertThat(response.data().description()).isEqualTo(category.description());
        assertThat(response.data().slug()).isEqualTo(category.slug());
        assertThat(response.data().isActive()).isEqualTo(category.isActive());
        assertThat(response.data().visible()).isEqualTo(dto.visible());
        assertThat(response.data().displayOrder()).isEqualTo(category.displayOrder());
        assertThat(response.data().createdAt().getSecond()).isEqualTo(category.createdAt().getSecond());
    }

    @Test
    void shouldUpdateCategoryButUpdateJustDisplayOrder() throws Exception {
        ResponseUserTest userData = helper.loginSuperAdm();
        CategoryDTO category = this.helper.createCategory(userData, null);

        UpdateCategoryDTO dto = new UpdateCategoryDTO(
                category.id(),
                null,
                null,
                null,
                null,
                null,
                99,
                null,
                null
        );

        MvcResult result = this.mockMvc.perform(patch(this.URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<CategoryDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<CategoryDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.data().id()).isEqualTo(category.id());
        assertThat(response.data().name()).isEqualTo(category.name());
        assertThat(response.data().description()).isEqualTo(category.description());
        assertThat(response.data().slug()).isEqualTo(category.slug());
        assertThat(response.data().isActive()).isEqualTo(category.isActive());
        assertThat(response.data().visible()).isEqualTo(category.visible());
        assertThat(response.data().displayOrder()).isEqualTo(dto.displayOrder());
        assertThat(response.data().createdAt().getSecond()).isEqualTo(category.createdAt().getSecond());
    }

    private static CreateCategoryDTO getCreateCategoryDTO(long parentId) {
        Random random = new Random();

        char letter = (char) ('a' + random.nextInt(26));
        char letter2 = (char) ('a' + random.nextInt(26));
        char letter3 = (char) ('a' + random.nextInt(26));
        char letter4 = (char) ('a' + random.nextInt(26));
        char letter5 = (char) ('a' + random.nextInt(26));
        char letter6 = (char) ('a' + random.nextInt(26));
        char letter7 = (char) ('a' + random.nextInt(26));

        return new CreateCategoryDTO(
                "software engineer " + letter + letter2 + letter3 + letter4 + letter5 + letter6 + letter7,
                "",
                "software-engineer-" + letter + letter2 + letter3 + letter4 + letter5 + letter6 + letter7,
                true,
                true,
                0,
                parentId
        );
    }

}
