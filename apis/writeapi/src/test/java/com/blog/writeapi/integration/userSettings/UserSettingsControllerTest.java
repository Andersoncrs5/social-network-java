package com.blog.writeapi.integration.userSettings;

import com.blog.writeapi.HelperTest;
import com.blog.writeapi.modules.user.repository.UserRepository;
import com.blog.writeapi.modules.userSettings.dto.UpdateUserSettingsDTO;
import com.blog.writeapi.modules.userSettings.dto.UserSettingsDTO;
import com.blog.writeapi.modules.userSettings.model.enums.ContentFilterLevelEnum;
import com.blog.writeapi.modules.userSettings.model.enums.FontSizeScaleEnum;
import com.blog.writeapi.modules.userSettings.model.enums.LanguageEnum;
import com.blog.writeapi.modules.userSettings.model.enums.ThemeEnum;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserSettingsControllerTest {
    private final String URL = "/v1/user-settings";

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
    void shouldUpdateAllField() throws Exception {
        ResponseUserTest userData = helper.createUser();

        UpdateUserSettingsDTO dto = new UpdateUserSettingsDTO(
                LanguageEnum.EN_US,
                ThemeEnum.DARK,
                !userData.tokens().settingsDTO().showOnlineStatus(),
                !userData.tokens().settingsDTO().notifyNewFollower(),
                !userData.tokens().settingsDTO().notifyLikes(),
                !userData.tokens().settingsDTO().notifyComments(),
                !userData.tokens().settingsDTO().notifyMentions(),
                !userData.tokens().settingsDTO().twoFactorEnabled(),
                !userData.tokens().settingsDTO().autoplayVideos(),
                !userData.tokens().settingsDTO().marketingEmailsAllowed(),
                20,
                ContentFilterLevelEnum.SAFE,
                FontSizeScaleEnum.LARGE,
                "UTC"
        );

        MvcResult result = mockMvc.perform(patch(this.URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<UserSettingsDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<UserSettingsDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.data()).isNotNull();
        assertThat(response.data().id()).isEqualTo(userData.tokens().settingsDTO().id());
        assertThat(response.data().language()).isEqualTo(dto.language());
        assertThat(response.data().theme()).isEqualTo(dto.theme());
        assertThat(response.data().showOnlineStatus()).isEqualTo(dto.showOnlineStatus());
        assertThat(response.data().notifyNewFollower()).isEqualTo(dto.notifyNewFollower());
        assertThat(response.data().notifyLikes()).isEqualTo(dto.notifyLikes());
        assertThat(response.data().notifyComments()).isEqualTo(dto.notifyComments());
        assertThat(response.data().notifyMentions()).isEqualTo(dto.notifyMentions());
        assertThat(response.data().twoFactorEnabled()).isEqualTo(dto.twoFactorEnabled());
        assertThat(response.data().autoplayVideos()).isEqualTo(dto.autoplayVideos());
        assertThat(response.data().marketingEmailsAllowed()).isEqualTo(dto.marketingEmailsAllowed());
        assertThat(response.data().itemsPerPage()).isEqualTo(dto.itemsPerPage());
        assertThat(response.data().contentFilterLevel()).isEqualTo(dto.contentFilterLevel());
        assertThat(response.data().fontSizeScale()).isEqualTo(dto.fontSizeScale());
        assertThat(response.data().timezone()).isEqualTo(dto.timezone());


    }

}
