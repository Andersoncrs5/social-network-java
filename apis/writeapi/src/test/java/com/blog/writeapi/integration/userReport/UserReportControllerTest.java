package com.blog.writeapi.integration.userReport;

import com.blog.writeapi.configs.HelperTest;
import com.blog.writeapi.modules.user.repository.UserRepository;
import com.blog.writeapi.modules.userReport.dto.CreateUserReportDTO;
import com.blog.writeapi.modules.userReport.dto.UserReportDTO;
import com.blog.writeapi.utils.enums.report.ReportReason;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserReportControllerTest {
    private final String URL = "/v1/user-report";

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
    void shouldCreateReport() throws Exception {
        ResponseUserTest reporter = helper.createUser();
        ResponseUserTest reportedUser = helper.createUser();

        CreateUserReportDTO dto = new CreateUserReportDTO(
                "AnyDesc",
                ReportReason.MISINFORMATION,
                reportedUser.userDTO().id()
        );

        MvcResult result = mockMvc.perform(post(this.URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + reporter.tokens().token()
                        ))
                .andExpect(status().isCreated()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<UserReportDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<UserReportDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.data()).isNotNull();

        assertThat(response.data().id()).isNotNull();
        assertThat(response.data().reporter().id()).isEqualTo(reporter.userDTO().id());
        assertThat(response.data().reportedUser().id()).isEqualTo(reportedUser.userDTO().id());
        assertThat(response.data().description()).isEqualTo(dto.description());
        assertThat(response.data().reason()).isEqualTo(dto.reason());

    }

    @Test
    void shouldReturnNotFoundBecauseUserNotFoundCreateReport() throws Exception {
        ResponseUserTest reporter = helper.createUser();

        CreateUserReportDTO dto = new CreateUserReportDTO(
                "AnyDesc",
                ReportReason.MISINFORMATION,
                (reporter.userDTO().id() + 1)
        );

        MvcResult result = mockMvc.perform(post(this.URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + reporter.tokens().token()
                        ))
                .andExpect(status().isNotFound()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<UserReportDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<UserReportDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);
        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturnConflictBecauseReportAlreadyExistsWhenCreateReport() throws Exception {
        ResponseUserTest reporter = helper.createUser();
        ResponseUserTest reportedUser = helper.createUser();

        this.helper.addReportToUser(reporter, reportedUser);

        CreateUserReportDTO dto = new CreateUserReportDTO(
                "AnyDesc",
                ReportReason.MISINFORMATION,
                reportedUser.userDTO().id()
        );

        MvcResult result = mockMvc.perform(post(this.URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + reporter.tokens().token()
                        ))
                .andExpect(status().isConflict()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<UserReportDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<UserReportDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);
        assertThat(response.data()).isNull();
    }

    @Test
    void shouldDeleteReport() throws Exception {
        ResponseUserTest reporter = helper.createUser();
        ResponseUserTest reportedUser = helper.createUser();

        UserReportDTO report = this.helper.addReportToUser(reporter, reportedUser);

        MvcResult result = mockMvc.perform(delete(this.URL+"/"+report.id())
                        .header("Authorization", "Bearer " + reporter.tokens().token()
                        ))
                .andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Void>> typeRef = new TypeReference<>() {};

        ResponseHttp<Void> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.data()).isNull();

    }

    @Test
    void shouldReturnNotFoundBecauseReportNotFoundWhenDeleteReport() throws Exception {
        ResponseUserTest reporter = helper.createUser();

        MvcResult result = mockMvc.perform(delete(this.URL+"/"+reporter.userDTO().id())
                        .header("Authorization", "Bearer " + reporter.tokens().token()
                        ))
                .andExpect(status().isNotFound()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Void>> typeRef = new TypeReference<>() {};

        ResponseHttp<Void> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);
        assertThat(response.data()).isNull();

    }

    @Test
    void shouldReturnForbBecauseAnotherUserTriedDeleteReport() throws Exception {
        ResponseUserTest reporter = helper.createUser();
        ResponseUserTest reportedUser = helper.createUser();
        ResponseUserTest userTest = helper.createUser();

        UserReportDTO report = this.helper.addReportToUser(reporter, reportedUser);

        MvcResult result = mockMvc.perform(delete(this.URL+"/"+report.id())
                    .header("Authorization", "Bearer " + userTest.tokens().token())
                ).andExpect(status().isForbidden()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Void>> typeRef = new TypeReference<>() {};

        ResponseHttp<Void> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);
        assertThat(response.data()).isNull();

    }

}
