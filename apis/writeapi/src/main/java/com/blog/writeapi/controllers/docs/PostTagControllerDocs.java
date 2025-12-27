package com.blog.writeapi.controllers.docs;

import com.blog.writeapi.dtos.postTag.CreatePostTagDTO;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface PostTagControllerDocs {
    String tag = "Post Tag";

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
