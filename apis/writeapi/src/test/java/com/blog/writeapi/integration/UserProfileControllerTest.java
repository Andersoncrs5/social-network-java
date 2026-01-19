package com.blog.writeapi.integration;

import com.blog.writeapi.HelperTest;
import com.blog.writeapi.dtos.userProfile.UpdateUserProfileDTO;
import com.blog.writeapi.dtos.userProfile.UserProfileDTO;
import com.blog.writeapi.models.enums.profile.ProfileVisibilityEnum;
import com.blog.writeapi.repositories.UserRepository;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.res.ResponseUserTest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class UserProfileControllerTest {

    private final String URL = "/v1/user-profile";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HelperTest helper;

    @BeforeEach
    void setup() {
        this.userRepository.deleteAll();
    }

    @Test
    void shouldUpdateAllFieldsOfProfile() throws Exception {
        ResponseUserTest userData = helper.createUser();

        UpdateUserProfileDTO dto = new UpdateUserProfileDTO(
                """
                        Hey-hey-hey
                        Yeah-ah
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
                    """,
                "https://i.scdn.co/image/ab67616d0000b2732d0e5ab5bd2e234fbcffa3e0",
                new HashSet<>(),
                ProfileVisibilityEnum.PUBLIC
        );

        MvcResult result = mockMvc.perform(patch(this.URL)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + userData.tokens().token()))
                .andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<UserProfileDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<UserProfileDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.traceId()).isNotBlank();

        assertThat(response.data().user().id()).isEqualTo(userData.userDTO().id());
        assertThat(response.data().bio()).isEqualTo(dto.bio());
    }

    @Test
    void shouldUpdateJustFieldAvatarUrlProfile() throws Exception {
        ResponseUserTest userData = helper.createUser();

        UpdateUserProfileDTO dto = new UpdateUserProfileDTO(
                null,
                "https://i.scdn.co/image/ab67616d0000b2732d0e5ab5bd2e234fbcffa3e0",
                null,
                null
        );

        MvcResult result = mockMvc.perform(patch(this.URL)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + userData.tokens().token()))
                .andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<UserProfileDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<UserProfileDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.traceId()).isNotBlank();

        assertThat(response.data().user().id()).isEqualTo(userData.userDTO().id());
        assertThat(response.data().bio()).isNotEqualTo(dto.bio());
        assertThat(response.data().websiteUrls()).isNotEqualTo(dto.websiteUrls());
        assertThat(response.data().avatarUrl()).isEqualTo(dto.avatarUrl());
        assertThat(response.data().visibility()).isEqualTo(ProfileVisibilityEnum.PUBLIC);
        assertThat(response.data().user().id()).isEqualTo(userData.userDTO().id());

    }

    @Test
    void shouldUpdateJustFieldVisibilityUrlProfile() throws Exception {
        ResponseUserTest userData = helper.createUser();

        UpdateUserProfileDTO dto = new UpdateUserProfileDTO(
                null,
                null,
                null,
                ProfileVisibilityEnum.PRIVATE
        );

        MvcResult result = mockMvc.perform(patch(this.URL)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + userData.tokens().token()))
                .andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<UserProfileDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<UserProfileDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.traceId()).isNotBlank();

        assertThat(response.data().user().id()).isEqualTo(userData.userDTO().id());
        assertThat(response.data().bio()).isNotEqualTo(dto.bio());
        assertThat(response.data().websiteUrls()).isNotEqualTo(dto.websiteUrls());
        assertThat(response.data().avatarUrl()).isNotEqualTo(dto.avatarUrl());
        assertThat(response.data().visibility()).isEqualTo(ProfileVisibilityEnum.PRIVATE);
        assertThat(response.data().user().id()).isEqualTo(userData.userDTO().id());

    }

    @Test
    void shouldUpdateJustFieldBioOfProfile() throws Exception {
        ResponseUserTest userData = helper.createUser();

        UpdateUserProfileDTO dto = new UpdateUserProfileDTO(
                """
                        Hey-hey-hey
                        Yeah-ah
                        Sheets of empty canvas, untouched sheets of clay
                        Were laid spread out before me, as her body once did
                        All five horizons revolved around her soul, as the Earth to the Sun
                        Now the air I tasted and breathed has taken a turn
                        Ooh, and all I taught her was everything
                        Mmm, oh, I know she gave me all that she wore
                        And now my bitter hands chafe beneath the clouds
                        Of what was everything
                        All the pictures had all been washed in black
                        Tattooed everythin
                    """,
                null,
                null,
                null
        );

        MvcResult result = mockMvc.perform(patch(this.URL)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + userData.tokens().token()))
                .andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<UserProfileDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<UserProfileDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.traceId()).isNotBlank();

        assertThat(response.data().user().id()).isEqualTo(userData.userDTO().id());
        assertThat(response.data().bio()).isEqualTo(dto.bio());
        assertThat(response.data().avatarUrl()).isNotEqualTo(dto.avatarUrl());
        assertThat(response.data().visibility()).isEqualTo(ProfileVisibilityEnum.PUBLIC);
        assertThat(response.data().websiteUrls()).isNotEqualTo(dto.websiteUrls());
    }

}
