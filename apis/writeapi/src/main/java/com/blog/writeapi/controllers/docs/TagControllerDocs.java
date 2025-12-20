package com.blog.writeapi.controllers.docs;

import com.blog.writeapi.dtos.tag.CreateTagDTO;
import com.blog.writeapi.dtos.tag.TagDTO;
import com.blog.writeapi.dtos.tag.UpdateTagDTO;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.res.swagger.tag.ResponseTagDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

public interface TagControllerDocs {
    String tag = "Tag";

    @PostMapping
    @Operation(summary = "Create new tag", tags = {tag})
    @PreAuthorize("hasAnyAuthority('SUPER_ADM_ROLE', 'ADM_ROLE')")
    @CircuitBreaker(name = "tagUpdateCB")
    @ApiResponse(responseCode = "201",
            description = "Create new tag",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseTagDTO.class)))
    ResponseEntity<@NonNull ResponseHttp<TagDTO>> create(@Valid @RequestBody CreateTagDTO dto, HttpServletRequest request);

    @GetMapping("/{id}")
    @Operation(summary = "Get one tag", tags = {tag})
    @CircuitBreaker(name = "tagGetCB")
    @ApiResponse(responseCode = "200",
            description = "Get one tag",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseTagDTO.class)))
    @ApiResponse(responseCode = "404",
            description = "return ResponseHttp with data empty and status code 404",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseHttp.class)))
    ResponseEntity<?> get(@PathVariable @IsId Long id, HttpServletRequest request);

    @DeleteMapping("/{id}")
    @CircuitBreaker(name = "tagDeleteCB")
    @Operation(summary = "Delete one tag", tags = {tag})
    @PreAuthorize("hasAnyAuthority('SUPER_ADM_ROLE', 'ADM_ROLE')")
    @ApiResponse(responseCode = "200",
            description = "Delete one tag",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseHttp.class)))
    @ApiResponse(responseCode = "404",
            description = "Return ResponseHttp with data empty and status code 404",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseHttp.class)))
    ResponseEntity<?> delete(@PathVariable @IsId Long id, HttpServletRequest request);

    @PatchMapping
    @Operation(summary = "Update one tag", tags = {tag})
    @PreAuthorize("hasAnyAuthority('SUPER_ADM_ROLE', 'ADM_ROLE')")
    @CircuitBreaker(name = "tagUpdateCB")
    @ApiResponse(responseCode = "200",
            description = "Update one tag",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseTagDTO.class)))
    @ApiResponse(responseCode = "404",
            description = "return ResponseHttp with data empty and status code 404",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseHttp.class)))
    ResponseEntity<?> patch(@Valid @RequestBody UpdateTagDTO dto, HttpServletRequest request);
}
