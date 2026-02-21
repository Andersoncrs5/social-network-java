package com.blog.writeapi.modules.reportPost.controller.doc;

import com.blog.writeapi.modules.reportPost.dto.CreatePostReportDTO;
import com.blog.writeapi.modules.reportPost.dto.UpdatePostReportDTO;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

public interface PostReportControllerDocs {
    @PostMapping
    ResponseEntity<?> create(
            @RequestBody @Valid CreatePostReportDTO dto,
            HttpServletRequest request
    );

    @DeleteMapping("{id}")
    ResponseEntity<?> delete(
            @PathVariable @IsId Long id,
            HttpServletRequest request
    );

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('MODERATOR_ROLE')")
    ResponseEntity<?> patch(
            @PathVariable @IsId Long id,
            @RequestBody @Valid UpdatePostReportDTO dto,
            HttpServletRequest request
    );
}
