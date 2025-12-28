package com.blog.writeapi.integration;

import com.blog.writeapi.HelperTest;
import com.blog.writeapi.dtos.post.CreatePostDTO;
import com.blog.writeapi.dtos.post.PostDTO;
import com.blog.writeapi.dtos.post.UpdatePostDTO;
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
public class PostControllerTest {
    private final String URL = "/v1/post";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostRepository repository;

    @Autowired
    private HelperTest helper;

    @BeforeEach
    void setup () {
        this.repository.deleteAll();
    }

    // CREATE
    @Test
    void shouldCreateAPost() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        CreatePostDTO dto = new CreatePostDTO(
                "All five horizons revolved around her soul, as the Earth to the Sun",
                "black-pearl-jem",
                """
                        Sheets of empty canvas, untouched sheets of clay
                        Were laid spread out before me, as her body once did
                        All five horizons revolved around her soul, as the Earth to the Sun
                        Now the air I tasted and breathed has taken a turn
                        Ooh, and all I taught her was everything
                        Mmm, oh, I know she gave me all that she wore
                        And now my bitter hands chafe beneath the clouds
                        Of what was everything
                        All the pictures had all been washed in black
                        Tattooed everything
                        I take a walk outside, I'm surrounded by some kids at play
                        I can feel their laughter, so why do I sear?
                        Mmm, oh, and twisted thoughts that spin 'round my head
                        I'm spinnin', oh-oh, I'm spinnin'
                        How quick the Sun can drop away
                        Now my bitter hands cradle broken glass
                        Of what was everything
                        All the pictures have all been washed in black
                        Tattooed everything
                        All the love gone bad turned my world to black
                        Tattooed all I see, all that I am
                        All I'll be, yeah-yeah
                        Oh-oh, oh-oh, ooh
                        I know someday you'll have a beautiful life, I know you'll be a star
                        In somebody else's sky, but why, why
                        Why can't it be, oh, can't it be mine?
                        Ooh, ah, yeah
                        Ah, ooh-ooh
                        Doo-doo-doo-doo, doo-doo-doo
                        Doo-doo-doo-doo, doo-doo-doo
                        Doo-doo-doo-doo, doo-doo-doo
                        Doo-doo-doo-doo, doo-doo-doo
                        Doo-doo-doo-doo, doo-doo-doo
                        Doo-doo-doo-doo, doo-doo-doo
                        Doo-doo-doo-doo, doo-doo-doo
                        Doo-doo-doo-doo, doo-doo-doo (oh, oh-yeah)
                        Doo-doo-doo-doo, doo-doo-doo
                        Doo-doo-doo-doo, doo-doo-doo (ah-ah)
                        Doo-doo-doo-doo, doo-doo-doo (ah-ah)
                        Doo-doo-doo-doo, doo-doo-doo
                        Doo-doo-doo-doo, doo-doo-doo (ah, yeah)
                        Doo-doo-doo-doo, doo-doo-doo (ah-ah, yeah)
                        Doo-doo-doo-doo, doo-doo-doo (yeah, ah)
                        Doo-doo-doo-doo, doo-doo-doo (yeah-yeah-yeah)
                        Doo-doo-doo-doo, doo-doo-doo
                        Doo-doo-doo-doo, doo-doo-doo (ooh-ooh)
                        Ooh-ooh, ooh-ooh
                        Doo-doo-doo-doo, doo-doo-doo
                        Ooh-ooh, ooh-ooh
                        Ooh-ooh""",
                5
        );

        MvcResult result = this.mockMvc.perform(post(this.URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().title()).isEqualTo(dto.title());
        assertThat(response.data().slug()).isEqualTo(dto.slug());
        assertThat(response.data().content()).isEqualTo(dto.content());
        assertThat(response.data().readingTime()).isEqualTo(dto.readingTime());

        assertThat(response.data().author().id()).isEqualTo(userData.userDTO().id());
    }

    // GET
    @Test
    void shouldGetAPost() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        PostDTO post = this.helper.createPost(userData);

        MvcResult result = this.mockMvc.perform(get(this.URL + "/" + post.id())
                .header("Authorization", "Bearer " + userData.tokens().token()
                ))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isEqualTo(post.id());
    }

    @Test
    void shouldReturnNotFoundWhenGetPostById() throws Exception {
        ResponseUserTest userData = this.helper.createUser();

        MvcResult result = this.mockMvc.perform(get(this.URL + "/" + 104657469563746526L)
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.data()).isNull();
        assertThat(response.status()).isEqualTo(false);
    }

    // DELETE
    @Test
    void shouldDeletePostById() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        PostDTO post = this.helper.createPost(userData);

        MvcResult result = this.mockMvc.perform(delete(this.URL + "/" + post.id())
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
    void shouldReturnForbWhenDeletePostById() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        ResponseUserTest userData2 = this.helper.createUser();
        PostDTO post = this.helper.createPost(userData);

        this.mockMvc.perform(delete(this.URL + "/" + post.id())
                        .header("Authorization", "Bearer " + userData2.tokens().token()
                        ))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    // UPDATE
    @Test
    void shouldReturnPostUpdate() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        PostDTO post = this.helper.createPost(userData);

        UpdatePostDTO dto = new UpdatePostDTO(
                "updated post with simple message",
                "updated-post-with-simple-message",
                """
                        aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                        aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                        aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                        aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                        aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                        aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                        aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                        aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                        aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                        """,
                12
        );

        MvcResult result = mockMvc.perform(patch(this.URL + "/" + post.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token())
                )
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isEqualTo(post.id());
        assertThat(response.data().content()).isEqualTo(dto.content());
        assertThat(response.data().title()).isEqualTo(dto.title());
        assertThat(response.data().readingTime()).isEqualTo(dto.readingTime());
        assertThat(response.data().slug()).isEqualTo(dto.slug());
        assertThat(response.data().createdAt().getSecond()).isEqualTo(post.createdAt().getSecond());
    }

    @Test
    void shouldReturnPostUpdateJustTitle() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        PostDTO post = this.helper.createPost(userData);

        UpdatePostDTO dto = new UpdatePostDTO(
                "updated post with simple message",
                null,
                null,
                null
        );

        MvcResult result = mockMvc.perform(patch(this.URL + "/" + post.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token())
                )
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isEqualTo(post.id());
        assertThat(response.data().content()).isEqualTo(post.content());
        assertThat(response.data().title()).isEqualTo(dto.title());
        assertThat(response.data().readingTime()).isEqualTo(post.readingTime());
        assertThat(response.data().slug()).isEqualTo(post.slug());
        assertThat(response.data().createdAt().getSecond()).isEqualTo(post.createdAt().getSecond());
    }

    @Test
    void shouldReturnPostUpdateJustSlug() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        PostDTO post = this.helper.createPost(userData);

        UpdatePostDTO dto = new UpdatePostDTO(
                null,
                "new-slug-updated",
                null,
                null
        );

        MvcResult result = mockMvc.perform(patch(this.URL + "/" + post.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token())
                )
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isEqualTo(post.id());
        assertThat(response.data().content()).isEqualTo(post.content());
        assertThat(response.data().title()).isEqualTo(post.title());
        assertThat(response.data().readingTime()).isEqualTo(post.readingTime());
        assertThat(response.data().slug()).isEqualTo(dto.slug());
        assertThat(response.data().createdAt().getSecond()).isEqualTo(post.createdAt().getSecond());
    }

    @Test
    void shouldReturnPostUpdateJustContent() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        PostDTO post = this.helper.createPost(userData);

        UpdatePostDTO dto = new UpdatePostDTO(
                null,
                null,
                """
                        aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                        aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                        aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                        aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                        aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                        aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                        aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                        aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                        aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                        """,
                null
        );

        MvcResult result = mockMvc.perform(patch(this.URL + "/" + post.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token())
                )
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isEqualTo(post.id());
        assertThat(response.data().content()).isEqualTo(dto.content());
        assertThat(response.data().title()).isEqualTo(post.title());
        assertThat(response.data().readingTime()).isEqualTo(post.readingTime());
        assertThat(response.data().slug()).isEqualTo(post.slug());
        assertThat(response.data().createdAt().getSecond()).isEqualTo(post.createdAt().getSecond());
    }

    @Test
    void shouldReturnPostUpdateJustReadingTime() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        PostDTO post = this.helper.createPost(userData);

        UpdatePostDTO dto = new UpdatePostDTO(
                null,
                null,
                null,
                9
        );

        MvcResult result = mockMvc.perform(patch(this.URL + "/" + post.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token())
                )
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isEqualTo(post.id());
        assertThat(response.data().content()).isEqualTo(post.content());
        assertThat(response.data().title()).isEqualTo(post.title());
        assertThat(response.data().readingTime()).isEqualTo(dto.readingTime());
        assertThat(response.data().slug()).isEqualTo(post.slug());
        assertThat(response.data().createdAt().getSecond()).isEqualTo(post.createdAt().getSecond());
    }

    @Test
    void shouldReturnForbBecauseAnotherUserTriedUpdatePost() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        ResponseUserTest userData2 = this.helper.createUser();

        PostDTO post = this.helper.createPost(userData);

        UpdatePostDTO dto = new UpdatePostDTO(
                null,
                null,
                null,
                9
        );

        MvcResult result = mockMvc.perform(patch(this.URL + "/" + post.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData2.tokens().token())
                )
                .andExpect(status().isForbidden())
                .andReturn();
    }


}
