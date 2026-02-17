package com.blog.writeapi.modules.reportType.controller.doc;

import com.blog.writeapi.modules.reportType.dto.CreateReportTypeDTO;
import com.blog.writeapi.modules.reportType.dto.UpdateReportTypeDTO;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.res.swagger.reportType.ResponseReportTypeDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Report Type", description = "Administrative endpoints for managing report categories and moderation types")
public interface ReportTypeControllerDocs {

    @CircuitBreaker(name = "tagUpdateCB")
    @Operation(
            summary = "Update an existing report type",
            description = "Modifies the metadata of a report type, such as its name, description, or default priority. Typically restricted to admin users."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Report type updated successfully",
                    content = @Content(schema = @Schema(implementation = ResponseReportTypeDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request data or malformed Snowflake ID"),
            @ApiResponse(responseCode = "403", description = "Forbidden: Insufficient privileges"),
            @ApiResponse(responseCode = "404", description = "Report type not found"),
            @ApiResponse(responseCode = "503", description = "Service unavailable (Circuit Breaker active)")
    })
    @PatchMapping("/{id}")
    ResponseEntity<?> update(
            @Parameter(description = "The unique Snowflake ID of the report type", example = "1998780200074176609")
            @PathVariable @IsId Long id,
            @RequestBody @Valid UpdateReportTypeDTO dto,
            HttpServletRequest request
    );

    @CircuitBreaker(name = "tagCreateCB")
    @Operation(
            summary = "Create a new report type",
            description = "Registers a new category for content reporting. These types will be listed for users when they flag a post or comment."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Report type created successfully",
                    content = @Content(schema = @Schema(implementation = ResponseReportTypeDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Validation failed for input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "503", description = "Service unavailable (Circuit Breaker active)")
    })
    @PostMapping
    ResponseEntity<?> create(
            @RequestBody @Valid CreateReportTypeDTO dto,
            HttpServletRequest request
    );

    @CircuitBreaker(name = "tagDeleteCB")
    @Operation(
            summary = "Delete a report type",
            description = "Soft or hard delete of a report type by its Snowflake ID. Caution: This may affect historical report data."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Report type deleted successfully",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid ID format",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(responseCode = "403", description = "Operation not permitted"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Report type not found",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(responseCode = "500", description = "Error internal in server"),
            @ApiResponse(responseCode = "503", description = "Service unavailable (Circuit Breaker active)")
    })
    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(
            @Parameter(description = "The unique Snowflake ID of the report type", example = "1998780200074176609")
            @PathVariable @IsId Long id,
            HttpServletRequest request
    );
}
