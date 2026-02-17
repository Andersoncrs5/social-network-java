package com.blog.writeapi.unit;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.reportType.dto.CreateReportTypeDTO;
import com.blog.writeapi.modules.reportType.model.ReportTypeModel;
import com.blog.writeapi.modules.reportType.repository.ReportTypeRepository;
import com.blog.writeapi.modules.reportType.services.provider.ReportTypeService;
import com.blog.writeapi.utils.enums.report.ReportPriority;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.mappers.ReportTypeMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportTypeServiceTest {

    @Mock private ReportTypeRepository repository;
    @Mock private Snowflake generator;
    @Mock private ReportTypeMapper mapper;

    @InjectMocks private ReportTypeService service;

    ReportTypeModel type = new ReportTypeModel().toBuilder()
            .id(1998780200074176609L)
            .name("report")
            .description("Desc")
            .defaultPriority(ReportPriority.LOW)
            .isActive(true)
            .isVisible(true)
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    @Test
    void shouldReturnReportWhenGetById() {
        when(repository.findById(this.type.getId()))
                .thenReturn(Optional.of(this.type));

        ReportTypeModel model = this.service.getByIdSimple(this.type.getId());

        assertThat(model.getId()).isEqualTo(this.type.getId());

        verify(repository, times(1)).findById(this.type.getId());
    }

    @Test
    @DisplayName("Should throw ModelNotFoundException when ReportType ID does not exist")
    void shouldThrowModelNotFoundExceptionWhenGetById() {
        Long nonExistentId = this.type.getId();
        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        ModelNotFoundException exception = assertThrows(ModelNotFoundException.class, () ->
                this.service.getByIdSimple(nonExistentId)
        );

        assertTrue(exception.getMessage().equalsIgnoreCase("Report type not found"));

        verify(repository, times(1)).findById(nonExistentId);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnTrueWhenExistsByName() {
        when(repository.existsByName(this.type.getName()))
                .thenReturn(true);

        Boolean exists = service.existsByName(type.getName());

        assertThat(exists).isTrue();

        verify(this.repository, times(1)).existsByName(anyString());
    }

    @Test
    void shouldDeleteType() {
        doNothing().when(repository).delete(type);

        this.service.delete(type);

        verify(repository, times(1)).delete(type);
    }

    @Test
    void shouldCreateReport() {
        CreateReportTypeDTO dto = new CreateReportTypeDTO(
                "a",
                "a",
                "a",
                "a",
                1,
                ReportPriority.LOW,
                true,
                true
        );

        when(generator.nextId())
                .thenReturn(type.getId());
        when(mapper.toModel(any(CreateReportTypeDTO.class)))
                .thenReturn(this.type);
        when(repository.save(any(ReportTypeModel.class)))
                .thenReturn(this.type);

        ReportTypeModel reportTypeModel = this.service.create(dto);

        assertThat(reportTypeModel.getId()).isEqualTo(type.getId());
        assertThat(reportTypeModel.getName()).isEqualTo(type.getName());

        verify(repository, times(1)).save(any(ReportTypeModel.class));
        verify(generator, times(1)).nextId();
    }

}
