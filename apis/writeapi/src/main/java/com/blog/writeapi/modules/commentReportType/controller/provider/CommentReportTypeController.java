package com.blog.writeapi.modules.commentReportType.controller.provider;

import com.blog.writeapi.modules.commentReport.model.CommentReportModel;
import com.blog.writeapi.modules.commentReport.service.provider.CommentReportService;
import com.blog.writeapi.modules.commentReportType.controller.doc.CommentReportTypeControllerDocs;
import com.blog.writeapi.modules.commentReportType.dto.CreateCommentReportTypeDTO;
import com.blog.writeapi.modules.commentReportType.model.CommentReportTypeModel;
import com.blog.writeapi.modules.commentReportType.service.provider.CommentReportTypeService;
import com.blog.writeapi.modules.reportType.model.ReportTypeModel;
import com.blog.writeapi.modules.reportType.services.provider.ReportTypeService;
import com.blog.writeapi.utils.classes.ResultToggle;
import com.blog.writeapi.utils.enums.global.ToggleEnum;
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
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/comment-report-type")
public class CommentReportTypeController implements CommentReportTypeControllerDocs {

    private final CommentReportService commentReportService;
    private final ReportTypeService reportTypeService;
    private final ITokenService tokenService;
    private final CommentReportTypeService service;

    @Override
    public ResponseEntity<ResponseHttp<Void>> toggle(
            @RequestBody @Valid CreateCommentReportTypeDTO dto,
            HttpServletRequest request
    ) {
        Long userID = this.tokenService.extractUserIdFromRequest(request);

        CommentReportModel commentReport = this.commentReportService.findByIdSimple(dto.reportId());
        ReportTypeModel reportType = this.reportTypeService.getByIdSimple(dto.typeId());

        ResultToggle<CommentReportTypeModel> toggle = this.service.toggle(commentReport, reportType, userID);

        String message = toggle.result() == ToggleEnum.ADDED
                ? "Report type added" : "Report type removed";

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