package com.blog.writeapi.modules.postTag.controller.docs;

import com.blog.writeapi.modules.postTag.dtos.CreatePostTagDTO;
import com.blog.writeapi.modules.postTag.dtos.UpdatePostTagDTO;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.res.swagger.postTag.ResponsePostTagDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface PostTagControllerDocs {
    String tag = "Post Tag";

    @PatchMapping("/{id}")
    @Operation(
            summary = "Update post-tag association metadata",
            description = "Updates specific properties of a tag link, such as visibility, active status, or display order. " +
                    "Requires post authorship permissions.",
            tags = {tag}
    )
    @CircuitBreaker(name = "tagUpdateCB")
    @ApiResponse(
            responseCode = "200",
            description = "Association updated successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponsePostTagDTO.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid update data or malformed Snowflake ID",
            content = @Content(mediaType = "application/json")
    )
    @ApiResponse(
            responseCode = "403",
            description = "Forbidden: Only the post author can update this association",
            content = @Content(mediaType = "application/json")
    )
    @ApiResponse(
            responseCode = "404",
            description = "Tag association ID not found",
            content = @Content(mediaType = "application/json")
    )
    ResponseEntity<?> update(
            @PathVariable @IsId Long id,
            @Valid @RequestBody UpdatePostTagDTO dto,
            HttpServletRequest request
    );

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Remove a tag from a post",
            description = "Deletes the association between a post and a tag using the association ID. " +
                    "Only the author of the post is allowed to perform this action.",
            tags = {tag}
    )
    @CircuitBreaker(name = "tagDeleteCB")
    @ApiResponse(
            responseCode = "200",
            description = "Tag successfully disassociated from the post",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseHttp.class)
            )
    )
    @ApiResponse(
            responseCode = "401",
            description = "Unauthorized: Authentication token is missing or invalid",
            content = @Content(mediaType = "application/json")
    )
    @ApiResponse(
            responseCode = "403",
            description = "Forbidden: You do not have permission to remove tags from this post",
            content = @Content(mediaType = "application/json")
    )
    @ApiResponse(
            responseCode = "404",
            description = "Association not found with the provided ID",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseHttp.class))
    )
    ResponseEntity<?> delete(
            @PathVariable @IsId Long id,
            HttpServletRequest request
    );

    @PostMapping
    @Operation(
            summary = "Associate a tag with a post",
            description = "Creates a link between an existing post and a tag. " +
                    "The system validates if the association already exists to prevent duplicates.",
            tags = {tag}
    )
    @CircuitBreaker(name = "tagCreateCB")
    @ApiResponse(
            responseCode = "201",
            description = "Tag successfully associated with the post",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponsePostTagDTO.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input data, invalid IDs, or Tag already associated with this Post",
            content = @Content(mediaType = "application/json")
    )
    @ApiResponse(
            responseCode = "401",
            description = "Unauthorized: Authentication token is missing or invalid",
            content = @Content(mediaType = "application/json")
    )
    @ApiResponse(responseCode = "403",
            description = "Forbidden: You are not the author of this post",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseHttp.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Post or Tag not found with the provided IDs",
            content = @Content(mediaType = "application/json")
    )
    ResponseEntity<?> create(
            @Valid @RequestBody CreatePostTagDTO dto,
            HttpServletRequest request
    );
}
