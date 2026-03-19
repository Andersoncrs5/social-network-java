package com.blog.writeapi.modules.commentReport.controller.doc;

import com.blog.writeapi.modules.commentReport.dto.CreateCommentReportDTO;
import com.blog.writeapi.modules.commentReport.dto.UpdateCommentReportDTO;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

public interface ICommentReportControllerDocs {
    @PostMapping
    ResponseEntity<?> create(
            @RequestBody @Valid CreateCommentReportDTO dto,
            HttpServletRequest request
    );
    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(
            @PathVariable @IsId Long id,
            HttpServletRequest request
    );
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('MODERATOR_ROLE')")
    ResponseEntity<?> update(
            @PathVariable @IsId Long id,
            @Valid @RequestBody UpdateCommentReportDTO dto,
            HttpServletRequest request
    );
}
