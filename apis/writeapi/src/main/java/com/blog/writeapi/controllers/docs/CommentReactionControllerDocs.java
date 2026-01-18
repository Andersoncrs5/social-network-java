package com.blog.writeapi.controllers.docs;

import com.blog.writeapi.dtos.commentReaction.CreateCommentReactionDTO;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.res.swagger.commentReaction.ResponseCommentReactionDTO;
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

@Tag(name = "CommentReaction", description = "Endpoints for managing user reactions on comments")
public interface CommentReactionControllerDocs {

    @PostMapping
    @Operation(
            summary = "Toggle comment reaction",
            description = """
                    Handles the reaction logic for a comment:
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
                    content = @Content(schema = @Schema(implementation = ResponseCommentReactionDTO.class))
            ),
            @ApiResponse(
                    responseCode = "201",
                    description = "Reaction applied successfully",
                    content = @Content(schema = @Schema(implementation = ResponseCommentReactionDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid data or Snowflake IDs",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Comment or Reaction type not found",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Service unavailable (Circuit Breaker active)",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            )
    })
    ResponseEntity<?> toggle(
            @Valid @RequestBody CreateCommentReactionDTO dto,
            HttpServletRequest request
    );
}