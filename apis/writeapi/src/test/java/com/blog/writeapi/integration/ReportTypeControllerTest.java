package com.blog.writeapi.integration;

import com.blog.writeapi.HelperTest;
import com.blog.writeapi.modules.postVote.dtos.PostVoteDTO;
import com.blog.writeapi.modules.reportType.dto.CreateReportTypeDTO;
import com.blog.writeapi.modules.reportType.dto.ReportTypeDTO;
import com.blog.writeapi.modules.reportType.dto.UpdateReportTypeDTO;
import com.blog.writeapi.modules.reportType.repository.ReportTypeRepository;
import com.blog.writeapi.utils.enums.report.ReportPriority;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ReportTypeControllerTest {

    private final String URL = "/v1/report-type";

    @Autowired
    private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;
    @Autowired private ReportTypeRepository repository;

    @Autowired private HelperTest helper;

    @BeforeEach void setup() {
        this.repository.deleteAll();
    }

    @Test
    void shouldCreateReportType() throws Exception {
        ResponseUserTest superAdm = this.helper.loginSuperAdm();

        CreateReportTypeDTO dto = new CreateReportTypeDTO(
                "name" + UUID.randomUUID(),
                "DescAny",
                null,
                "#0000000",
                1,
                ReportPriority.HIGH,
                true,
                true
        );

        MvcResult result = this.mockMvc.perform(post(this.URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Bearer " + superAdm.tokens().token())
        ).andExpect(status().isCreated()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<ReportTypeDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<ReportTypeDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data()).isNotNull();

        assertThat(response.data().id()).isPositive().isNotZero();
        assertThat(response.data().color()).isEqualTo(dto.color());
        assertThat(response.data().name()).isEqualTo(dto.name());
        assertThat(response.data().description()).isEqualTo(dto.description());
        assertThat(response.data().icon()).isEqualTo(dto.icon());
        assertThat(response.data().color()).isEqualTo(dto.color());
        assertThat(response.data().displayOrder()).isEqualTo(dto.displayOrder());
        assertThat(response.data().defaultPriority()).isEqualTo(dto.defaultPriority());
        assertThat(response.data().isActive()).isEqualTo(dto.isActive());
        assertThat(response.data().isVisible()).isEqualTo(dto.isVisible());
    }

    @Test
    void shouldDeleteReportType() throws Exception {
        ResponseUserTest superAdm = this.helper.loginSuperAdm();
        ReportTypeDTO reportTypeDTO = this.helper.createReportType(superAdm);

        MvcResult result = this.mockMvc.perform(delete(this.URL + "/" + reportTypeDTO.id())
                .header("Authorization", "Bearer " + superAdm.tokens().token())
        ).andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturnNotFoundWhenDeleteReportType() throws Exception {
        ResponseUserTest superAdm = this.helper.loginSuperAdm();
        ReportTypeDTO reportTypeDTO = this.helper.createReportType(superAdm);

        MvcResult result = this.mockMvc.perform(delete(this.URL + "/" + (reportTypeDTO.id() + 1))
                .header("Authorization", "Bearer " + superAdm.tokens().token())
        ).andExpect(status().isNotFound()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturnReportTypeWhenUpdateReportType() throws Exception {
        ResponseUserTest superAdm = this.helper.loginSuperAdm();
        ReportTypeDTO reportTypeDTO = this.helper.createReportType(superAdm);

        UpdateReportTypeDTO dto = new UpdateReportTypeDTO(
                reportTypeDTO.name() + "updated",
                reportTypeDTO.description() + "updated",
                reportTypeDTO.icon() + "updated",
                reportTypeDTO.color() + "0",
                10,
                ReportPriority.CRITICAL,
                !reportTypeDTO.isActive(),
                !reportTypeDTO.isVisible()
        );

        MvcResult result = this.mockMvc.perform(patch(this.URL + "/" + reportTypeDTO.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Bearer " + superAdm.tokens().token())
        ).andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<ReportTypeDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<ReportTypeDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data()).isNotNull();

        assertThat(response.data().id()).isEqualTo(reportTypeDTO.id());
        assertThat(response.data().color()).isEqualTo(dto.color());
        assertThat(response.data().name()).isEqualTo(dto.name());
        assertThat(response.data().description()).isEqualTo(dto.description());
        assertThat(response.data().icon()).isEqualTo(dto.icon());
        assertThat(response.data().color()).isEqualTo(dto.color());
        assertThat(response.data().displayOrder()).isEqualTo(dto.displayOrder());
        assertThat(response.data().defaultPriority()).isEqualTo(dto.defaultPriority());
        assertThat(response.data().isActive()).isEqualTo(dto.isActive());
        assertThat(response.data().isVisible()).isEqualTo(dto.isVisible());
    }

    @Test
    void shouldReturnNullWhenUpdateReportType() throws Exception {
        ResponseUserTest superAdm = this.helper.loginSuperAdm();
        ReportTypeDTO reportTypeDTO = this.helper.createReportType(superAdm);

        UpdateReportTypeDTO dto = new UpdateReportTypeDTO(
                reportTypeDTO.name() + "updated",
                reportTypeDTO.description() + "updated",
                reportTypeDTO.icon() + "updated",
                reportTypeDTO.color() + "0",
                10,
                ReportPriority.CRITICAL,
                !reportTypeDTO.isActive(),
                !reportTypeDTO.isVisible()
        );

        MvcResult result = this.mockMvc.perform(patch(this.URL + "/" + (reportTypeDTO.id() + 1))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Bearer " + superAdm.tokens().token())
        ).andExpect(status().isNotFound()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();
        assertThat(response.status()).isFalse();

        assertThat(response.data()).isNull();
    }


}
