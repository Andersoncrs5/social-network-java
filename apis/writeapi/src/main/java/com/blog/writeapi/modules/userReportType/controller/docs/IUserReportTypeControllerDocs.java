package com.blog.writeapi.modules.userReportType.controller.docs;

import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.userReportType.dto.CreateUserReportTypeDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface IUserReportTypeControllerDocs {
    @PostMapping("/toggle")
    ResponseEntity<?> create(
            @RequestBody @Valid CreateUserReportTypeDTO dto,
            @AuthenticationPrincipal UserPrincipal principal
    );
}
