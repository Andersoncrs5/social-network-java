package com.blog.writeapi.modules.userReportType.controller.provider;

import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.userReportType.controller.docs.IUserReportTypeControllerDocs;
import com.blog.writeapi.modules.userReportType.dto.CreateUserReportTypeDTO;
import com.blog.writeapi.modules.userReportType.model.UserReportTypeModel;
import com.blog.writeapi.modules.userReportType.service.interfaces.IUserReportTypeService;
import com.blog.writeapi.utils.classes.ResultToggle;
import com.blog.writeapi.utils.enums.global.ToggleEnum;
import com.blog.writeapi.utils.res.ResponseHttp;
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
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user-report-type")
public class UserReportTypeController implements IUserReportTypeControllerDocs {

    private final IUserReportTypeService service;

    public ResponseEntity<?> create(
            @RequestBody @Valid CreateUserReportTypeDTO dto,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        ResultToggle<UserReportTypeModel> toggle = this.service.toggle(
                dto.reportId(),
                dto.typeId(),
                principal.getId()
        );

        String message = toggle.result() == ToggleEnum.ADDED
                ? "Report added" : "Report removed";

        HttpStatus status = toggle.result() == ToggleEnum.ADDED
                ? HttpStatus.CREATED : HttpStatus.OK;

        return ResponseEntity.status(status).body(new ResponseHttp<Void>(
                null,
                message,
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        ));
    }

}
