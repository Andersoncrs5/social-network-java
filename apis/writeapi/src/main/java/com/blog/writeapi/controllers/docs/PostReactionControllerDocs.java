package com.blog.writeapi.controllers.docs;

import com.blog.writeapi.dtos.postReaction.CreatePostReactionDTO;
import com.blog.writeapi.dtos.postReaction.PostReactionDTO;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.res.swagger.postReaction.ResponsePostReactionDTO;
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

@Tag(name = "PostReaction", description = "Endpoints for managing user reactions (emojis) on posts")
public interface PostReactionControllerDocs {

    @PostMapping
    @Operation(
            summary = "Toggle post reaction",
            description = """
                    Handles the reaction logic for a post:
                    1. If the user hasn't reacted: Creates a new reaction (201).
                    2. If the user clicks the same reaction: Removes it (200).
                    3. If the user clicks a different reaction: Updates the existing one (200).
                    """
    )
    @CircuitBreaker(name = "tag-toggle-cb")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reaction removed or changed successfully",
                    content = @Content(schema = @Schema(implementation = ResponsePostReactionDTO.class))
            ),
            @ApiResponse(
                    responseCode = "201",
                    description = "Reaction applied successfully",
                    content = @Content(schema = @Schema(implementation = ResponsePostReactionDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data or malformed Snowflake IDs",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Post or Reaction type not found",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Service temporarily unavailable (Circuit Breaker active)",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            )
    })
    ResponseEntity<ResponseHttp<PostReactionDTO>> toggle(
            @Valid @RequestBody CreatePostReactionDTO dto,
            HttpServletRequest request
    );

}