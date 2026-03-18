package com.blog.writeapi.modules.postReportType.controller.doc;

import com.blog.writeapi.modules.postReportType.dto.CreatePostReportTypeDTO;
import com.blog.writeapi.utils.res.ResponseHttp;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Post Report Assignment", description = "Endpoints for managing types and categories assigned to a post report")
public interface IPostReportTypeControllerDocs {

    @Operation(
            summary = "Toggle a report type for a specific report",
            description = "Assigns or removes a report type (reason) from an existing post report. " +
                    "This operation is only permitted within 10 minutes of the initial report creation."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Report type added successfully",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "Report type removed successfully",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or Snowflake ID format",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Post report or Report type not found",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "410",
                    description = "Reporting window expired (10-minute limit exceeded)",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error internal in server",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Service temporarily unavailable (Circuit Breaker active)",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            )
    })
    @PostMapping("/toggle")
    @CircuitBreaker(name = "tag-toggle-cb")
    ResponseEntity<?> toggle(
            @RequestBody @Valid CreatePostReportTypeDTO dto,
            HttpServletRequest request
    );
}