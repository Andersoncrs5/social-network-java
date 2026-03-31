package com.blog.writeapi.integration;

import com.blog.writeapi.configs.HelperTest;
import com.blog.writeapi.modules.followers.dtos.FollowersDTO;
import com.blog.writeapi.modules.followers.dtos.UpdateFollowersDTO;
import com.blog.writeapi.modules.followers.repository.FollowersRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class FollowersControllerTest {

    private final String URL = "/v1/follow";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired private FollowersRepository repository;

    @Autowired
    private HelperTest helper;

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }

    @Test
    void shouldReturnFollowUpdateAllFields() throws Exception {
        ResponseUserTest followData = this.helper.createUser();
        ResponseUserTest followingData = this.helper.createUser();

        FollowersDTO followersDTO = this.helper.followUser(followData, followingData);

        UpdateFollowersDTO dto = new UpdateFollowersDTO(
                !followersDTO.isMuted(),
                !followersDTO.notifyPosts(),
                !followersDTO.notifyComments()
        );

        MvcResult result = this.mockMvc.perform(patch(this.URL + "/" + followersDTO.id())
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + followData.tokens().token()
                        ))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<FollowersDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<FollowersDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.status()).isTrue();
        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();

        assertThat(response.data()).isNotNull();
        assertThat(response.data().id()).isEqualTo(followersDTO.id());
        assertThat(response.data().notifyPosts()).isEqualTo(dto.notifyPosts());
        assertThat(response.data().isMuted()).isEqualTo(dto.isMuted());
        assertThat(response.data().notifyComments()).isEqualTo(dto.notifyComments());
        assertThat(response.data().following().id()).isEqualTo(followersDTO.following().id());
        assertThat(response.data().follower().id()).isEqualTo(followersDTO.follower().id());
    }

    @Test
    void shouldReturnForbBecauseAnotherUserTriedUpdate() throws Exception {

        ResponseUserTest followData = this.helper.createUser();
        ResponseUserTest usrData = this.helper.createUser();
        ResponseUserTest followingData = this.helper.createUser();

        FollowersDTO followersDTO = this.helper.followUser(followData, followingData);

        UpdateFollowersDTO dto = new UpdateFollowersDTO(
                !followersDTO.isMuted(),
                !followersDTO.notifyPosts(),
                !followersDTO.notifyComments()
        );

        this.mockMvc.perform(patch(this.URL + "/" + followersDTO.id())
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + usrData.tokens().token()
                        ))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnNotFoundTheUpdateFollow() throws Exception {
        ResponseUserTest followData = this.helper.createUser();
        ResponseUserTest followingData = this.helper.createUser();

        FollowersDTO followersDTO = this.helper.followUser(followData, followingData);

        UpdateFollowersDTO dto = new UpdateFollowersDTO(
                !followersDTO.isMuted(),
                !followersDTO.notifyPosts(),
                !followersDTO.notifyComments()
        );

        MvcResult result = this.mockMvc.perform(patch(this.URL + "/" + (followersDTO.id() + 1 ) )
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + followData.tokens().token()
                        ))
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.status()).isFalse();
        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldFollowUser() throws Exception {
        ResponseUserTest followData = this.helper.createUser();
        ResponseUserTest followingData = this.helper.createUser();

        MvcResult result = this.mockMvc.perform(post(this.URL + "/" + followingData.userDTO().id() + "/toggle")
                        .header("Authorization", "Bearer " + followData.tokens().token()
                        ))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<FollowersDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<FollowersDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.data()).isNotNull();
        assertThat(response.status()).isTrue();
        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();

        assertThat(response.data().id()).isNotZero().isPositive();
        assertThat(response.data().follower().id()).isEqualTo(followData.userDTO().id());
        assertThat(response.data().following().id()).isEqualTo(followingData.userDTO().id());
        assertThat(response.data().isMuted()).isFalse();
        assertThat(response.data().notifyPosts()).isTrue();
        assertThat(response.data().notifyComments()).isTrue();

    }

    @Test
    void shouldUnfollowUser() throws Exception {
        ResponseUserTest followData = this.helper.createUser();
        ResponseUserTest followingData = this.helper.createUser();

        FollowersDTO followersDTO = this.helper.followUser(followData, followingData);

        MvcResult result = this.mockMvc.perform(post(this.URL + "/" + followingData.userDTO().id() + "/toggle")
                        .header("Authorization", "Bearer " + followData.tokens().token()
                        ))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.status()).isTrue();
        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturnUserNotFound() throws Exception {
        ResponseUserTest followData = this.helper.createUser();

        MvcResult result = this.mockMvc.perform(post(this.URL+"/1998780200074176109/toggle")
                        .header("Authorization", "Bearer " + followData.tokens().token()
                        ))
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.data()).isNull();
        assertThat(response.status()).isFalse();
        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();
    }

    @Test
    void shouldReturnBadRequestBecauseFollowYourself() throws Exception {
        ResponseUserTest followData = this.helper.createUser();

        MvcResult result = this.mockMvc.perform(post(this.URL + "/" + followData.userDTO().id() + "/toggle")
                        .header("Authorization", "Bearer " + followData.tokens().token()
                        ))
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.data()).isNull();
        assertThat(response.status()).isFalse();
        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();

    }

}
