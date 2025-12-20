package com.blog.writeapi.controllers.docs;

import com.blog.writeapi.dtos.tag.CreateTagDTO;
import com.blog.writeapi.utils.res.swagger.tag.ResponseTagDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface TagControllerDocs {
    String tag = "Tag";

    @PostMapping
    @Operation(summary = "Create new tag", tags = {tag})
    @PreAuthorize("hasAnyAuthority('SUPER_ADM_ROLE', 'ADM_ROLE')")
    @ApiResponse(responseCode = "201",
            description = "Create new tag",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseTagDTO.class)))
    ResponseEntity<?> create(@Valid @RequestBody CreateTagDTO dto, HttpServletRequest request);
}
