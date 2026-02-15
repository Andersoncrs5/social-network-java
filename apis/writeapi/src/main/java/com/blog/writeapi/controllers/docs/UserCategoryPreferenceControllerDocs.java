package com.blog.writeapi.controllers.docs;

import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.res.swagger.postFavorite.ResponsePostFavoriteDTO;
import com.blog.writeapi.utils.res.swagger.userCategoryPreference.ResponseUserCategoryPreferenceDTO;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Tag(name = "UserCategoryPreference", description = "Endpoints for managing Category Preference")
public interface UserCategoryPreferenceControllerDocs {

    @PostMapping("/{categoryId}/toggle")
    @CircuitBreaker(name = "tag-toggle-cb")
    @Operation(
            summary = "To added category in user preference",
            description = "Toggle to category a user"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Category removed with successfully"
            ),
            @ApiResponse(
                    responseCode = "201",
                    description = "Category added with successfully",
                    content = @Content(schema = @Schema(implementation = ResponseUserCategoryPreferenceDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data or malformed Snowflake ID",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found",
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
            @PathVariable @IsId Long categoryId,
            HttpServletRequest request
    );
}
