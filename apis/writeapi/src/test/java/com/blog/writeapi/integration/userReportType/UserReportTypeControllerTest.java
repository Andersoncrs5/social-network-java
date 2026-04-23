package com.blog.writeapi.integration.userReportType;

import com.blog.writeapi.configs.HelperTest;
import com.blog.writeapi.configs.TestContainerConfig;
import com.blog.writeapi.modules.reportType.dto.ReportTypeDTO;
import com.blog.writeapi.modules.userReport.dto.UserReportDTO;
import com.blog.writeapi.modules.userReport.repository.UserReportRepository;
import com.blog.writeapi.modules.userReportType.dto.CreateUserReportTypeDTO;
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
@Import(TestContainerConfig.class)
public class UserReportTypeControllerTest {
    private final String URL = "/v1/user-report-type";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private UserReportRepository repository;

    @Autowired
    private HelperTest helper;

    @BeforeEach
    void setup() {
        this.repository.deleteAll();
    }

    @Test
    void shouldAddedReportTypeToUserReport() throws Exception {
        ResponseUserTest superAdm = this.helper.loginSuperAdm();

        ResponseUserTest reporter = this.helper.createUser();
        ResponseUserTest reportedUser = this.helper.createUser();

        ReportTypeDTO reportTypeDTO = this.helper.createReportType(superAdm);
        UserReportDTO userReportDTO = this.helper.addReportToUser(reporter, reportedUser);

        CreateUserReportTypeDTO dto = new CreateUserReportTypeDTO(
                userReportDTO.id(),
                reportTypeDTO.id()
        );

        MvcResult result = mockMvc.perform(post(this.URL + "/toggle")
                .header("Authorization", "Bearer " + reporter.tokens().token())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isCreated()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Void>> typeRef = new TypeReference<>() {};

        ResponseHttp<Void> response = objectMapper.readValue(json, typeRef);

        assertThat(response.data()).isNull();
        assertThat(response.status()).isTrue();
        assertThat(response.message()).isNotBlank();

    }

    @Test
    void shouldRemoveReportTypeToUserReport() throws Exception {
        ResponseUserTest superAdm = this.helper.loginSuperAdm();

        ResponseUserTest reporter = this.helper.createUser();
        ResponseUserTest reportedUser = this.helper.createUser();

        ReportTypeDTO reportTypeDTO = this.helper.createReportType(superAdm);
        UserReportDTO userReportDTO = this.helper.addReportToUser(reporter, reportedUser);
        this.helper.addReportTypeToUserReport(userReportDTO, reportTypeDTO, reporter);

        CreateUserReportTypeDTO dto = new CreateUserReportTypeDTO(
                userReportDTO.id(),
                reportTypeDTO.id()
        );

        MvcResult result = mockMvc.perform(post(this.URL + "/toggle")
                .header("Authorization", "Bearer " + reporter.tokens().token())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Void>> typeRef = new TypeReference<>() {};

        ResponseHttp<Void> response = objectMapper.readValue(json, typeRef);

        assertThat(response.data()).isNull();
        assertThat(response.status()).isTrue();
        assertThat(response.message()).isNotBlank();
    }

    @Test
    void shouldReturnNotFoundBecauseUserReportNotFound() throws Exception {
        ResponseUserTest superAdm = this.helper.loginSuperAdm();

        ResponseUserTest reporter = this.helper.createUser();

        ReportTypeDTO reportTypeDTO = this.helper.createReportType(superAdm);

        CreateUserReportTypeDTO dto = new CreateUserReportTypeDTO(
                (reportTypeDTO.id() + 1),
                reportTypeDTO.id()
        );

        MvcResult result = mockMvc.perform(post(this.URL + "/toggle")
                .header("Authorization", "Bearer " + reporter.tokens().token())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isNotFound()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Void>> typeRef = new TypeReference<>() {};

        ResponseHttp<Void> response = objectMapper.readValue(json, typeRef);

        assertThat(response.data()).isNull();
        assertThat(response.status()).isFalse();
        assertThat(response.message()).isNotBlank();
    }

}
