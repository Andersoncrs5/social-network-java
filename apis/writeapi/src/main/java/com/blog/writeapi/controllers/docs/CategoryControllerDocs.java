package com.blog.writeapi.controllers.docs;

import com.blog.writeapi.dtos.category.CreateCategoryDTO;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.res.classes.category.ResponseCategoryDTO;
import com.blog.writeapi.utils.res.classes.user.ResponseUserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

public interface CategoryControllerDocs {
    String tag = "Category";

    @PostMapping
    @Operation(summary = "Create new category", tags = {tag})
    @PreAuthorize("hasAnyAuthority('SUPER_ADM_ROLE', 'ADM_ROLE')")
    @ApiResponse(responseCode = "201",
            description = "Create new category",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseCategoryDTO.class)))
    @ApiResponse(responseCode = "404",
            description = "Category not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseHttp.class)))
    ResponseEntity<?> create(@Valid @RequestBody CreateCategoryDTO dto, HttpServletRequest request);

    @GetMapping("/{id}")
    @Operation(summary = "Get one category by id", tags = {tag})
    @ApiResponse(responseCode = "200",
            description = "Get category",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseCategoryDTO.class)))
    @ApiResponse(responseCode = "404",
            description = "Category not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseHttp.class)))
    ResponseEntity<?> get(
            @PathVariable
            @Positive(message = "The ID provided must be a positive number.")
            Long id,
            HttpServletRequest request
    ) ;

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete one category by id", tags = {tag})
    @PreAuthorize("hasAnyAuthority('SUPER_ADM_ROLE', 'ADM_ROLE')")
    @ApiResponse(responseCode = "200",
            description = "Delete category",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseHttp.class)))
    @ApiResponse(responseCode = "404",
            description = "Category not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseHttp.class)))
    ResponseEntity<?> delete(
            @PathVariable
            @Positive(message = "The ID provided must be a positive number.")
            Long id,
            HttpServletRequest request
    );

}
