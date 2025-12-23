package com.blog.writeapi;

import cn.hutool.core.lang.UUID;
import com.blog.writeapi.dtos.category.CategoryDTO;
import com.blog.writeapi.dtos.category.CreateCategoryDTO;
import com.blog.writeapi.dtos.post.CreatePostDTO;
import com.blog.writeapi.dtos.post.PostDTO;
import com.blog.writeapi.dtos.tag.CreateTagDTO;
import com.blog.writeapi.dtos.tag.TagDTO;
import com.blog.writeapi.dtos.user.CreateUserDTO;
import com.blog.writeapi.dtos.user.LoginUserDTO;
import com.blog.writeapi.dtos.user.UserDTO;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.res.ResponseTokens;
import com.blog.writeapi.utils.res.ResponseUserTest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.OffsetDateTime;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Service
@RequiredArgsConstructor
public class HelperTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public PostDTO createPost(ResponseUserTest userData) throws Exception {
        String URL = "/v1/post";

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

        MvcResult result = this.mockMvc.perform(post(URL)
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

        return response.data();
    }

    public TagDTO createTag(ResponseUserTest userData) throws Exception {
        String URL = "/v1/tag";
        String chars = this.generateChars();

        var dto = new CreateTagDTO(
                "software engineer " + chars,
                "software-engineer-" + chars,
                "",
                true,
                true,
                true
        );

        MvcResult result = mockMvc.perform(post(URL)
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

        return response.data();
    }

    public CategoryDTO createCategory(ResponseUserTest userData, Long parentId) throws Exception {
        String URL = "/v1/category";
        String chars = this.generateChars();

        CreateCategoryDTO dto = new CreateCategoryDTO(
                "software engineer " + chars,
                "",
                "software-engineer-" + chars,
                true,
                true,
                0,
                parentId
        );

        MvcResult result = this.mockMvc.perform(post(URL)
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

        return response.data();
    }

    public ResponseUserTest loginSuperAdm() throws Exception {
        String URL = "/v1/auth/";

        LoginUserDTO dto = new LoginUserDTO(
                "system.domain@gmail.com",
                "0123456789"
        );

        MvcResult result = mockMvc.perform(post(URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<ResponseTokens>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<ResponseTokens> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(true);
        assertThat(response.message()).isNotBlank();
        assertThat(response.data().token()).isNotBlank();
        assertThat(response.data().refreshToken()).isNotBlank();


        MvcResult resultGet = mockMvc.perform(get("/v1/user/me")
                        .header("Authorization", "Bearer " + response.data().token()))
                .andExpect(status().isOk()).andReturn();

        String json = resultGet.getResponse().getContentAsString();
        TypeReference<ResponseHttp<UserDTO>> typeRefGet = new TypeReference<>() {};

        ResponseHttp<UserDTO> responseGet =
                objectMapper.readValue(json, typeRefGet);

        return new ResponseUserTest(
                response.data(),
                null,
                responseGet.data()
        );

    }

    public ResponseUserTest createUser() {
        try {
            String key = UUID.fastUUID().toString();

            CreateUserDTO dto = new CreateUserDTO(
                    "name" + key,
                    "username" + key,
                    "user" + key + "@gmail.com",
                    "12345678"
            );

            MvcResult result = mockMvc.perform(post("/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andReturn();

            String registerJson = result.getResponse().getContentAsString();
            TypeReference<ResponseHttp<ResponseTokens>> typeRef =
                    new TypeReference<>() {
                    };

            ResponseHttp<ResponseTokens> response =
                    objectMapper.readValue(registerJson, typeRef);

            assertThat(response.status()).isEqualTo(true);
            assertThat(response.message()).isNotBlank();
            assertThat(response.data().token()).isNotBlank();
            assertThat(response.data().refreshToken()).isNotBlank();

            MvcResult resultGet = mockMvc.perform(get("/v1/user/me")
                            .header("Authorization", "Bearer " + response.data().token()))
                    .andExpect(status().isOk()).andReturn();

            String json = resultGet.getResponse().getContentAsString();
            TypeReference<ResponseHttp<UserDTO>> typeRefGet = new TypeReference<>() {};

            ResponseHttp<UserDTO> responseGet =
                    objectMapper.readValue(json, typeRefGet);

            return new ResponseUserTest(
                    response.data(),
                    dto,
                    responseGet.data()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String generateChars() {
        Random random = new Random();
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            char l = (char) ('a' + random.nextInt(26));
            builder.append(l);
        }

        return builder.toString();
    }


}
