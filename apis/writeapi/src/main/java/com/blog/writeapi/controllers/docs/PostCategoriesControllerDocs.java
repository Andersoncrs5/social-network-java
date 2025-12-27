package com.blog.writeapi.controllers.docs;

import com.blog.writeapi.dtos.postCategories.CreatePostCategoriesDTO;
import com.blog.writeapi.dtos.postCategories.UpdatePostCategoriesDTO;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.res.ResponseHttp;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface PostCategoriesControllerDocs {
    String tag = "Post Categories";

    @PostMapping
    @Operation(
            summary = "Associate a category with a post",
            description = "Links a specific category to an existing post, defining display order and primary status.",
            tags = {tag}
    )
    @CircuitBreaker(name = "tagCreateCB")
    @ApiResponse(responseCode = "201",
            description = "Category successfully associated with the post",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseHttp.class)))
    @ApiResponse(responseCode = "400",
            description = "Invalid input data or Snowflake ID format",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "401",
            description = "Unauthorized: Authentication token is missing or invalid",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "409",
            description = "Conflict: This category is already linked to this post",
            content = @Content(mediaType = "application/json"))
    ResponseEntity<?> create(@Valid @RequestBody CreatePostCategoriesDTO dto, HttpServletRequest request);

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Remove category from post",
            description = "Deletes the association between a post and a category. Requires author permissions.",
            tags = {tag}
    )
    @CircuitBreaker(name = "tagDeleteCB")
    @ApiResponse(responseCode = "200",
            description = "Association removed successfully",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "403",
            description = "Forbidden: Only the post author can remove categories",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404",
            description = "Association ID not found",
            content = @Content(mediaType = "application/json"))
    ResponseEntity<?> del(@PathVariable @IsId Long id, HttpServletRequest request);

    @GetMapping("/{id}")
    @Operation(
            summary = "Get association details",
            description = "Returns the specific details of a post-category link, including metadata.",
            tags = {tag}
    )
    @CircuitBreaker(name = "tagGetCB")
    @ApiResponse(responseCode = "200",
            description = "Association details retrieved successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseHttp.class)))
    @ApiResponse(responseCode = "404",
            description = "Association not found",
            content = @Content(mediaType = "application/json"))
    ResponseEntity<?> get(@PathVariable @IsId Long id, HttpServletRequest request);

    @PatchMapping("/{id}")
    @Operation(
            summary = "Update association properties",
            description = "Updates display order, active status, or primary flag. Changing to primary will demote the previous primary category.",
            tags = {tag}
    )
    @CircuitBreaker(name = "tagUpdateCB")
    @ApiResponse(responseCode = "200",
            description = "Association updated successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseHttp.class)))
    @ApiResponse(responseCode = "403",
            description = "Forbidden: Insufficient permissions to update this post",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "409",
            description = "Conflict: Error while swapping primary category status",
            content = @Content(mediaType = "application/json"))
    ResponseEntity<?> update(
            @PathVariable @IsId Long id,
            @Valid @RequestBody UpdatePostCategoriesDTO dto,
            HttpServletRequest request
    );
}
