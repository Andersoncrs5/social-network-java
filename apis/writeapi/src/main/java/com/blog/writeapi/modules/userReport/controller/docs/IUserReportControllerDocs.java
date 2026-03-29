package com.blog.writeapi.modules.userReport.controller.docs;

import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.userReport.dto.CreateUserReportDTO;
import com.blog.writeapi.modules.userReport.dto.UpdateUserReportDTO;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

public interface IUserReportControllerDocs {
    @PostMapping
    ResponseEntity<?> create(
            @RequestBody @Valid CreateUserReportDTO dto,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest request
    );
    @DeleteMapping("{id}")
    ResponseEntity<?> delete(
            @PathVariable @IsId Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest request
    );
    @PatchMapping("{id}")
    ResponseEntity<?> patch(
            @PathVariable @IsId Long id,
            @RequestBody @Valid UpdateUserReportDTO dto,
            @AuthenticationPrincipal UserPrincipal principal
    );
}
