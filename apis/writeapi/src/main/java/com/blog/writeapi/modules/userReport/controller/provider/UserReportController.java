package com.blog.writeapi.modules.userReport.controller.provider;

import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.userReport.controller.docs.IUserReportControllerDocs;
import com.blog.writeapi.modules.userReport.dto.CreateUserReportDTO;
import com.blog.writeapi.modules.userReport.dto.UpdateUserReportDTO;
import com.blog.writeapi.modules.userReport.model.UserReportModel;
import com.blog.writeapi.modules.userReport.service.docs.IUserReportService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.mappers.UserReportMapper;
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
@RequestMapping("/v1/user-report")
public class UserReportController implements IUserReportControllerDocs {
    private final IUserReportService service;
    private final UserReportMapper mapper;

    @Override
    public ResponseEntity<?> create(
            @RequestBody @Valid CreateUserReportDTO dto,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest request
    ) {
        boolean exists = this.service.existsByReportedUserIdAndReporterId(dto.reportedUserId(), principal.getId());

        if (exists) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseHttp<>(
                    null,
                    "You already report this post!",
                    UUID.randomUUID().toString(),
                    1,
                    false,
                    OffsetDateTime.now()
            ));
        }

        UserReportModel model = this.service.create(dto, dto.reportedUserId(), principal.getId());

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                new ResponseHttp<>(
                    this.mapper.toDTO(model),
                    "Report created with successfully",
                    UUID.randomUUID().toString(),
                    1,
                    true,
                    OffsetDateTime.now()
                )
            );
    }

    @Override
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest request
    ) {
        UserReportModel reportModel = this.service.findByIdSimple(id);

        this.service.delete(reportModel, principal.getId());

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(
                new ResponseHttp<>(
                    null,
                    "Report deleted with successfully",
                    UUID.randomUUID().toString(),
                    1,
                    true,
                    OffsetDateTime.now()
                )
            );
    }

    @Override
    public ResponseEntity<?> patch(
            @PathVariable @IsId Long id,
            @RequestBody @Valid UpdateUserReportDTO dto,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        UserReportModel report = this.service.findByIdSimple(id);
        UserReportModel update = this.service.update(dto, report, principal.getId());

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHttp<>(
                this.mapper.toDTO(update),
                "Report updated with successfully",
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        ));
    }

}
