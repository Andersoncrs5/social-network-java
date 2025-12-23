package com.blog.writeapi.controllers.docs;

import com.blog.writeapi.dtos.post.CreatePostDTO;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.res.swagger.post.ResponsePostDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface PostControllerDocs {
    String tag = "Post";

    @PostMapping
    @Operation(summary = "Create new post", tags = {tag})
    @CircuitBreaker(name = "tagCreateCB")
    @ApiResponse(responseCode = "201",
            description = "Create new post",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponsePostDTO.class)))
    @ApiResponse(responseCode = "401",
            description = "Unauthorized: Invalid or expired token",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseHttp.class)))
    ResponseEntity<?> create(@Valid @RequestBody CreatePostDTO dto, HttpServletRequest request);

    @GetMapping("/{id}")
    @Operation(summary = "Get one post", tags = {tag})
    @CircuitBreaker(name = "tagGetCB")
    @ApiResponse(responseCode = "200",
            description = "Get one post",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponsePostDTO.class)))
    @ApiResponse(responseCode = "404",
            description = "Return status 404",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseHttp.class)))
    @ApiResponse(responseCode = "401",
            description = "Unauthorized: Invalid or expired token",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseHttp.class)))
    ResponseEntity<?> get(@PathVariable @IsId Long id, HttpServletRequest request);

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a post by ID",
            description = "Deletes a post from the database. Only the author of the post is allowed to perform this action.",
            tags = {tag}
    )
    @CircuitBreaker(name = "tagDeleteCB")
    @ApiResponse(responseCode = "200",
            description = "Post successfully deleted",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseHttp.class)))
    @ApiResponse(responseCode = "404",
            description = "Post not found with the provided ID",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseHttp.class)))
    @ApiResponse(responseCode = "403",
            description = "Forbidden: You are not the author of this post",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseHttp.class)))
    @ApiResponse(responseCode = "401",
            description = "Unauthorized: Invalid or expired token",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseHttp.class)))
    ResponseEntity<?> del(@PathVariable @IsId Long id, HttpServletRequest request);
}
