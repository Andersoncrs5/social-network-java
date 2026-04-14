package com.blog.writeapi.integration.story;

import com.blog.writeapi.configs.HelperTest;
import com.blog.writeapi.configs.TestContainerConfig;
import com.blog.writeapi.modules.stories.dto.StoryDTO;
import com.blog.writeapi.modules.stories.repository.StoryRepository;
import com.blog.writeapi.modules.user.repository.UserRepository;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.res.ResponseUserTest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestContainerConfig.class)
public class StoryControllerTest {
    private final String URL = "/v1/story";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired private StoryRepository repository;
    @Autowired private UserRepository userRepository;

    @Autowired
    private HelperTest helper;

    @BeforeEach
    void setup () {
        this.repository.deleteAll();
        this.userRepository.deleteAll();
    }

    @Test
    void shouldCreateNewStory() throws Exception {
        ResponseUserTest userData = this.helper.createUser();

        Path path = Paths.get("src/test/java/com/blog/writeapi/utils/resources/foto.png");
        byte[] content = Files.readAllBytes(path);

        MockMultipartFile filePart = new MockMultipartFile(
                "file",
                "image" + HelperTest.generateChars() + ".png",
                "image/png",
                content
        );

        MvcResult result = this.mockMvc.perform(multipart(this.URL)
                        .file(filePart)
                        .param("fileName", "pochita-wallpaper")
                        .param("contentType", "image/png")
                        .param("isPublic", "true")
                        .param("isVisible", "true")
                        .param("backgroundColor", "#FFFFFF")
                        .param("caption", "Black")
                        .header("Authorization", "Bearer " + userData.tokens().token()))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        ResponseHttp<StoryDTO> response = objectMapper.readValue(json, new TypeReference<>() {});

        assertThat(response.data().getFileName()).isEqualTo("pochita-wallpaper");
        assertThat(response.data().getStorageKey()).isNotBlank();
        assertThat(response.data().getContentType()).isEqualTo("image/png");
        assertThat(response.data().getFileSize()).isEqualTo(filePart.getBytes().length);

        assertThat(response.status()).isTrue();

    }

}
