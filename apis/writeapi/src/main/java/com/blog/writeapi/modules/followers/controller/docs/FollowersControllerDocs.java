package com.blog.writeapi.modules.followers.controller.docs;

import com.blog.writeapi.configs.security.UserPrincipal;
import com.blog.writeapi.modules.followers.dtos.UpdateFollowersDTO;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.res.swagger.follow.ResponseFollowerDTO;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Followers", description = "Operations related to the social graph (Follow/Unfollow)")
public interface FollowersControllerDocs {

    @Operation(
            summary = "Toggle follow status",
            description = "Allows a user to follow or unfollow another user. If the relationship exists, it will be deleted (Unfollow). If not, it will be created (Follow)."
    )
    @CircuitBreaker(name = "tag-toggle-cb")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully unfollowed user",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "201",
                    description = "Successfully followed user",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseFollowerDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid Snowflake ID or business rule violation (e.g., self-follow)"),
            @ApiResponse(responseCode = "403", description = "Forbidden: User not authenticated or mismatch"),
            @ApiResponse(responseCode = "404", description = "Target user (followingId) not found"),
            @ApiResponse(responseCode = "503", description = "Service unavailable - Circuit Breaker open")
    })
    @PostMapping("/{followingId}/toggle")
    ResponseEntity<?> toggle(
            @Parameter(description = "Snowflake ID of the user to be followed/unfollowed", example = "1543216789012345")
            @PathVariable @IsId Long followingId,
            HttpServletRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    );

    @Operation(
            summary = "Update follow preferences",
            description = "Allows the follower to update notification settings (mute, notify_posts, notify_comments) for a specific follow relationship."
    )
    @CircuitBreaker(name = "tagUpdateCB")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Preferences updated successfully",
                    content = @Content(schema = @Schema(implementation = ResponseFollowerDTO.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid token"),
            @ApiResponse(responseCode = "403", description = "Resource Owner Mismatch: You don't own this follow relationship"),
            @ApiResponse(responseCode = "404", description = "Follow relationship ID not found")
    })
    @PatchMapping("/{id}")
    ResponseEntity<?> update(
            @Parameter(description = "ID of the follow relationship (Not the user ID)", example = "1543216789012345")
            @PathVariable @IsId Long id,
            @RequestBody @Valid UpdateFollowersDTO dto,
            HttpServletRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    );
}