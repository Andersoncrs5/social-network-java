package com.blog.writeapi.modules.postReportType.controller.provider;

import com.blog.writeapi.modules.postReportType.controller.doc.IPostReportTypeControllerDocs;
import com.blog.writeapi.modules.postReportType.dto.CreatePostReportTypeDTO;
import com.blog.writeapi.modules.postReportType.model.PostReportTypeModel;
import com.blog.writeapi.modules.postReportType.service.provider.PostReportTypeService;
import com.blog.writeapi.modules.reportPost.model.PostReportModel;
import com.blog.writeapi.modules.reportPost.services.provider.PostReportService;
import com.blog.writeapi.modules.reportType.model.ReportTypeModel;
import com.blog.writeapi.modules.reportType.services.provider.ReportTypeService;
import com.blog.writeapi.utils.classes.ResultToggle;
import com.blog.writeapi.utils.enums.global.ToggleEnum;
import com.blog.writeapi.utils.exceptions.BusinessRuleException;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.services.interfaces.ITokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/post-report-type")
public class PostReportTypeController implements IPostReportTypeControllerDocs {

    private final PostReportService postReportService;
    private final ReportTypeService reportTypeService;
    private final ITokenService tokenService;
    private final PostReportTypeService service;

    @Override
    public ResponseEntity<?> toggle(
            @RequestBody @Valid CreatePostReportTypeDTO dto,
            HttpServletRequest request
    ) {
        Long userID = this.tokenService.extractUserIdFromRequest(request);
        PostReportModel postReport = this.postReportService.findByIdSimple(dto.reportId());
        ReportTypeModel reportType = this.reportTypeService.getByIdSimple(dto.typeId());

        if (!Objects.equals(postReport.getUser().getId(), userID)) {
            throw new BusinessRuleException("This report is not your");
        }

        OffsetDateTime expirationLimit = postReport.getCreatedAt().plusMinutes(10);
        if (OffsetDateTime.now().isAfter(expirationLimit)) {
            throw new BusinessRuleException("The reporting window has expired. You can only add report types within 10 minutes of creation.");
        }

        ResultToggle<PostReportTypeModel> toggle = this.service.toggle(postReport, reportType);

        String message = toggle.result() == ToggleEnum.ADDED
                ? "Report added" : "Report removed";

        HttpStatus status = toggle.result() == ToggleEnum.ADDED
                ? HttpStatus.CREATED : HttpStatus.OK;

        return ResponseEntity.status(status).body(new ResponseHttp<>(
            null,
            message,
            UUID.randomUUID().toString(),
            1,
            true,
            OffsetDateTime.now()
        ));
    }

}
