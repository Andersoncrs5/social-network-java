package com.blog.writeapi.controllers.docs;

import com.blog.writeapi.dtos.userProfile.UpdateUserProfileDTO;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.res.swagger.userProfile.ResponseUserProfile;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "UserProfile", description = "Endpoints for managing user personal profile information")
public interface UserProfileControllerDocs {

    @PatchMapping
    @Operation(
            summary = "Update user profile",
            description = """
                    Updates the profile information of the authenticated user.
                    The user identity is resolved automatically via the access token.
                    """
    )
    @CircuitBreaker(name = "userProfileUpdateCB")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Profile updated successfully",
                    content = @Content(schema = @Schema(implementation = ResponseUserProfile.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data or validation error",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or expired token",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User or Profile not found",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Service temporarily unavailable (Circuit Breaker active)",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            )
    })
    ResponseEntity<?> update(
            @Valid @RequestBody UpdateUserProfileDTO dto,
            HttpServletRequest request
    );

}
