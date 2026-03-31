package com.blog.writeapi.modules.postReportType.controller.provider;

import com.blog.writeapi.configs.security.UserPrincipal;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final PostReportTypeService service;

    @Override
    public ResponseEntity<?> toggle(
            @RequestBody @Valid CreatePostReportTypeDTO dto,
            HttpServletRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Long userID = principal.getId();
        PostReportModel postReport = this.postReportService.findByIdSimple(dto.reportId());
        ReportTypeModel reportType = this.reportTypeService.getByIdSimple(dto.typeId());

        ResultToggle<PostReportTypeModel> toggle = this.service.toggle(postReport, reportType, userID);

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
