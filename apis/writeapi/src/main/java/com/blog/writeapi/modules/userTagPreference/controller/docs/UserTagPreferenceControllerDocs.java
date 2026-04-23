package com.blog.writeapi.modules.userTagPreference.controller.docs;

import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.res.swagger.userTagPreference.ResponseUserTagPreferenceDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "UserTagPreferenceControllerDocs", description = "Endpoints for managing Tag Preference")
public interface UserTagPreferenceControllerDocs {

    @PostMapping("/{tagID}/toggle")
    @CircuitBreaker(name = "tag-toggle-cb")
    @Operation(
            summary = "To added tag in user preference",
            description = "Toggle to tag a user"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tag removed with successfully"
            ),
            @ApiResponse(
                    responseCode = "201",
                    description = "Tag added with successfully",
                    content = @Content(schema = @Schema(implementation = ResponseUserTagPreferenceDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data or malformed Snowflake ID",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Tag not found",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Service temporarily unavailable (Circuit Breaker active)",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            )
    })
    ResponseEntity<?> toggle(
            @Parameter(description = "The Snowflake ID of the comment", example = "1998780200074176609")
            @PathVariable @IsId Long tagID,
            HttpServletRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    );
}