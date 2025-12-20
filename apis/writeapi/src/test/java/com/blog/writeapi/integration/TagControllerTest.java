package com.blog.writeapi.integration;

import com.blog.writeapi.HelperTest;
import com.blog.writeapi.dtos.tag.CreateTagDTO;
import com.blog.writeapi.dtos.tag.TagDTO;
import com.blog.writeapi.dtos.tag.UpdateTagDTO;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetDateTime;
import java.util.Random;

@SpringBootTest
@AutoConfigureMockMvc
public class TagControllerTest {
    private final String URL = "/v1/tag";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TagRepository repository;

    @Autowired
    private HelperTest helper;

    @BeforeEach
    void setup() {
        this.repository.deleteAll();
    }

    @Test
    void shouldCreateTag() throws Exception {
        ResponseUserTest userData = helper.loginSuperAdm();

        CreateTagDTO dto = this.getCreateTagDTO();

        MvcResult result = mockMvc.perform(post(this.URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<TagDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<TagDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.data().name()).isEqualTo(dto.name());
        assertThat(response.data().slug()).isEqualTo(dto.slug());
        assertThat(response.data().description()).isEqualTo(dto.description());
        assertThat(response.data().isActive()).isEqualTo(dto.isActive());
        assertThat(response.data().isVisible()).isEqualTo(dto.isVisible());
        assertThat(response.data().isSystem()).isEqualTo(dto.isSystem());
        assertThat(response.data().postsCount()).isEqualTo(0L);
        assertThat(response.data().createdAt().getMinute()).isEqualTo(OffsetDateTime.now().getMinute());
    }

    // GET
    @Test
    void shouldGetTagById() throws Exception {
        ResponseUserTest userData = this.helper.loginSuperAdm();
        TagDTO tag = this.helper.createTag(userData);

        MvcResult result = mockMvc.perform(get(this.URL + "/" + tag.id())
                .header("Authorization", "Bearer " + userData.tokens().token()
                )).andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<TagDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<TagDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.data().name()).isEqualTo(tag.name());
        assertThat(response.data().slug()).isEqualTo(tag.slug());
        assertThat(response.data().description()).isEqualTo(tag.description());
        assertThat(response.data().isActive()).isEqualTo(tag.isActive());
        assertThat(response.data().isVisible()).isEqualTo(tag.isVisible());
        assertThat(response.data().isSystem()).isEqualTo(tag.isSystem());
        assertThat(response.data().postsCount()).isEqualTo(tag.postsCount());
        assertThat(response.data().createdAt().getSecond()).isEqualTo(tag.createdAt().getSecond());
    }

    @Test
    void shouldReturnNullGetTagById() throws Exception {
        ResponseUserTest userData = this.helper.loginSuperAdm();

        MvcResult result = mockMvc.perform(get(this.URL + "/" + 1998780200074176609L)
                .header("Authorization", "Bearer " + userData.tokens().token()
                )).andExpect(status().isNotFound()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);
        assertThat(response.data()).isNull();
    }

    // DELETE
    @Test
    void shouldDeleteTagById() throws Exception {
        ResponseUserTest userData = this.helper.loginSuperAdm();
        TagDTO tag = this.helper.createTag(userData);

        MvcResult result = mockMvc.perform(delete(this.URL + "/" + tag.id())
                .header("Authorization", "Bearer " + userData.tokens().token()
                )).andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturnNotFoundWhenTryToDeleteTagById() throws Exception {
        ResponseUserTest userData = this.helper.loginSuperAdm();

        MvcResult result = mockMvc.perform(delete(this.URL + "/" + 1998780200074176609L)
                .header("Authorization", "Bearer " + userData.tokens().token()
                )).andExpect(status().isNotFound()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);
        assertThat(response.data()).isNull();
    }

    // UPDATE
    @Test
    void shouldUpdateAllFieldsOfTag() throws Exception {
        ResponseUserTest userData = this.helper.loginSuperAdm();
        TagDTO tag = this.helper.createTag(userData);

        UpdateTagDTO dto = new UpdateTagDTO(
                tag.id(),
                "name tag updated",
                "slug-tag-updated",
                "Desc updated",
                !tag.isActive(),
                !tag.isVisible(),
                !tag.isSystem(),
                OffsetDateTime.now()
        );

        MvcResult result = mockMvc.perform(patch(this.URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Bearer " + userData.tokens().token())
        )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<TagDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<TagDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();

        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isEqualTo(dto.id());
        assertThat(response.data().name()).isEqualTo(dto.name());
        assertThat(response.data().slug()).isEqualTo(dto.slug());
        assertThat(response.data().description()).isEqualTo(dto.description());
        assertThat(response.data().isActive()).isEqualTo(dto.isActive());
        assertThat(response.data().isVisible()).isEqualTo(dto.isVisible());
        assertThat(response.data().isSystem()).isEqualTo(dto.isSystem());
        assertThat(response.data().postsCount()).isEqualTo(tag.postsCount());
        assertThat(response.data().createdAt().getSecond()).isEqualTo(tag.createdAt().getSecond());
        assertThat(response.data().lastUsedAt().getSecond()).isEqualTo(dto.lastUsedAt().getSecond());
    }

    @Test
    void shouldUpdateOnlyName() throws Exception {
        ResponseUserTest userData = helper.loginSuperAdm();
        TagDTO tag = helper.createTag(userData);

        UpdateTagDTO dto = new UpdateTagDTO(
                tag.id(),
                "updated name",
                null,
                null,
                null,
                null,
                null,
                null
        );

        TagDTO updated = performUpdate(dto, userData);

        assertThat(updated.name()).isEqualTo(dto.name());
        assertThat(updated.slug()).isEqualTo(tag.slug());
        assertThat(updated.description()).isEqualTo(tag.description());
        assertThat(updated.isActive()).isEqualTo(tag.isActive());
    }

    @Test
    void shouldUpdateOnlySlug() throws Exception {
        ResponseUserTest userData = helper.loginSuperAdm();
        TagDTO tag = helper.createTag(userData);

        UpdateTagDTO dto = new UpdateTagDTO(
                tag.id(),
                null,
                "new-slug",
                null,
                null,
                null,
                null,
                null
        );

        TagDTO updated = performUpdate(dto, userData);

        assertThat(updated.slug()).isEqualTo(dto.slug());
        assertThat(updated.name()).isEqualTo(tag.name());
    }

    @Test
    void shouldUpdateOnlyDescription() throws Exception {
        ResponseUserTest userData = helper.loginSuperAdm();
        TagDTO tag = helper.createTag(userData);

        UpdateTagDTO dto = new UpdateTagDTO(
                tag.id(),
                null,
                null,
                "New description",
                null,
                null,
                null,
                null
        );

        TagDTO updated = performUpdate(dto, userData);

        assertThat(updated.description()).isEqualTo(dto.description());
        assertThat(updated.name()).isEqualTo(tag.name());
    }

    @Test
    void shouldUpdateOnlyIsActive() throws Exception {
        ResponseUserTest userData = helper.loginSuperAdm();
        TagDTO tag = helper.createTag(userData);

        UpdateTagDTO dto = new UpdateTagDTO(
                tag.id(),
                null,
                null,
                null,
                !tag.isActive(),
                null,
                null,
                null
        );

        TagDTO updated = performUpdate(dto, userData);

        assertThat(updated.isActive()).isEqualTo(dto.isActive());
        assertThat(updated.isVisible()).isEqualTo(tag.isVisible());
    }

    @Test
    void shouldUpdateOnlyIsVisible() throws Exception {
        ResponseUserTest userData = helper.loginSuperAdm();
        TagDTO tag = helper.createTag(userData);

        UpdateTagDTO dto = new UpdateTagDTO(
                tag.id(),
                null,
                null,
                null,
                null,
                !tag.isVisible(),
                null,
                null
        );

        TagDTO updated = performUpdate(dto, userData);

        assertThat(updated.isVisible()).isEqualTo(dto.isVisible());
        assertThat(updated.isActive()).isEqualTo(tag.isActive());
    }

    @Test
    void shouldUpdateOnlyIsSystem() throws Exception {
        ResponseUserTest userData = helper.loginSuperAdm();
        TagDTO tag = helper.createTag(userData);

        UpdateTagDTO dto = new UpdateTagDTO(
                tag.id(),
                null,
                null,
                null,
                null,
                null,
                !tag.isSystem(),
                null
        );

        TagDTO updated = performUpdate(dto, userData);

        assertThat(updated.isSystem()).isEqualTo(dto.isSystem());
    }

    @Test
    void shouldUpdateOnlyLastUsedAt() throws Exception {
        ResponseUserTest userData = helper.loginSuperAdm();
        TagDTO tag = helper.createTag(userData);

        OffsetDateTime now = OffsetDateTime.now();

        UpdateTagDTO dto = new UpdateTagDTO(
                tag.id(),
                null,
                null,
                null,
                null,
                null,
                null,
                now
        );

        TagDTO updated = performUpdate(dto, userData);

        assertThat(updated.lastUsedAt().getSecond())
                .isEqualTo(now.getSecond());
    }

    private TagDTO performUpdate(UpdateTagDTO dto, ResponseUserTest userData) throws Exception {

        MvcResult result = mockMvc.perform(patch(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token())
                )
                .andExpect(status().isOk())
                .andReturn();

        ResponseHttp<TagDTO> response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {}
        );

        return response.data();
    }

    public CreateTagDTO getCreateTagDTO() {
        Random random = new Random();

        char l = (char) ('a' + random.nextInt(26));
        char l2 = (char) ('a' + random.nextInt(26));
        char l3 = (char) ('a' + random.nextInt(26));
        char l4 = (char) ('a' + random.nextInt(26));
        char l5 = (char) ('a' + random.nextInt(26));
        char l6 = (char) ('a' + random.nextInt(26));
        char l7 = (char) ('a' + random.nextInt(26));
        char l8 = (char) ('a' + random.nextInt(26));
        char l9 = (char) ('a' + random.nextInt(26));
        char l10 = (char) ('a' + random.nextInt(26));

        return new CreateTagDTO(
                "software engineer " + l + l2 + l3 + l4 + l5 + l6 + l7 + l8 + l9 + l10,
                "software-engineer-" + l + l2 + l3 + l4 + l5 + l6 + l7 + l8 + l9 + l10,
                "",
                true,
                true,
                true
        );
    }

}
