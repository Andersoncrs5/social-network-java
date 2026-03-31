package com.blog.writeapi.integration;

import com.blog.writeapi.HelperTest;
import com.blog.writeapi.modules.category.dtos.CategoryDTO;
import com.blog.writeapi.modules.userCategoryPreference.dtos.UserCategoryPreferenceDTO;
import com.blog.writeapi.modules.category.repository.CategoryRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserCategoryPreferenceControllerTest {
    private final String URL = "/v1/user-category-preference";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private HelperTest helper;

    @Autowired private CategoryRepository categoryRepository;

    @BeforeEach
    void setup() {
        this.categoryRepository.deleteAll();
    }

    @Test
    void shouldCreatePreferenceIntoUser() throws Exception {
        ResponseUserTest userData = this.helper.createUser();

        ResponseUserTest master = helper.loginSuperAdm();
        CategoryDTO category = this.helper.createCategory(master, null);

        MvcResult result = this.mockMvc.perform(post(this.URL + "/" + category.id() + "/toggle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        )).andExpect(status().isCreated()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<UserCategoryPreferenceDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<UserCategoryPreferenceDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.data().id()).isNotZero().isPositive().isNotNull();
        assertThat(response.data().category().id()).isEqualTo(category.id());
        assertThat(response.data().user().id()).isEqualTo(userData.userDTO().id());
    }

    @Test
    void shouldRemovePreferenceIntoUser() throws Exception {
        ResponseUserTest userData = this.helper.createUser();

        ResponseUserTest master = helper.loginSuperAdm();
        CategoryDTO category = this.helper.createCategory(master, null);
        this.helper.addCategoryInPreferenceUser(userData, category);

        MvcResult result = this.mockMvc.perform(post(this.URL + "/" + category.id() + "/toggle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        )).andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturnNotFoundWhenCreatePreferenceIntoUser() throws Exception {
        ResponseUserTest userData = this.helper.createUser();

        ResponseUserTest master = helper.loginSuperAdm();
        CategoryDTO category = this.helper.createCategory(master, null);

        MvcResult result = this.mockMvc.perform(post(this.URL + "/" + (category.id() + 1) + "/toggle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        )).andExpect(status().isNotFound()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);
        assertThat(response.data()).isNull();
    }

}
