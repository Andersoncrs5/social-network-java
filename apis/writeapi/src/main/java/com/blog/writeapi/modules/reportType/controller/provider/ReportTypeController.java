package com.blog.writeapi.modules.reportType.controller.provider;

import com.blog.writeapi.modules.reportType.controller.doc.ReportTypeControllerDocs;
import com.blog.writeapi.modules.reportType.dto.CreateReportTypeDTO;
import com.blog.writeapi.modules.reportType.dto.UpdateReportTypeDTO;
import com.blog.writeapi.modules.reportType.model.ReportTypeModel;
import com.blog.writeapi.modules.reportType.services.interfaces.IReportTypeService;
import com.blog.writeapi.modules.user.service.docs.IUserService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.mappers.ReportTypeMapper;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.services.interfaces.ITokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/report-type")
public class ReportTypeController implements ReportTypeControllerDocs {

    private final IReportTypeService service;
    private final ReportTypeMapper mapper;

    @Override
    public ResponseEntity<?> create(
            @RequestBody @Valid CreateReportTypeDTO dto,
            HttpServletRequest request
    ) {
        ReportTypeModel typeCreated = this.service.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseHttp<>(
                this.mapper.toDTO(typeCreated),
                "Report Type created successfully",
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        ));
    }

    @Override
    public ResponseEntity<?> delete(
            @PathVariable @IsId Long id,
            HttpServletRequest request
    ) {
        ReportTypeModel type = this.service.getByIdSimple(id);

        this.service.delete(type);

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHttp<>(
                null,
                "Report Type deleted successfully",
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        ));
    }

    @Override
    public ResponseEntity<?> update(
            @PathVariable @IsId Long id,
            @RequestBody @Valid UpdateReportTypeDTO dto,
            HttpServletRequest request
    ) {
        ReportTypeModel type = this.service.getByIdSimple(id);

        ReportTypeModel typeUpdated = this.service.update(type, dto);

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHttp<>(
                this.mapper.toDTO(typeUpdated),
                "Report Type updated successfully",
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        ));
    }
}
