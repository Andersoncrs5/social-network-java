package com.blog.writeapi.controllers.docs;

import com.blog.writeapi.dtos.commentVote.ToggleCommentVoteDTO;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.res.swagger.commentVote.ResponseCommentVoteDTO;
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

@Tag(name = "CommentVote", description = "Endpoints for managing votes on comments")
public interface CommentVoteControllerDocs {

    @PostMapping
    @Operation(
            summary = "Toggle comment vote (Upvote/Downvote)",
            description = """
                    Handles the voting logic for a comment. If the same vote type is sent, it removes the vote.
                    If a different type is sent, it updates the vote. If no vote exists, it creates one.
                    """
    )
    @CircuitBreaker(name = "comment-vote-toggle-cb")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Comment vote removed or updated successfully",
                    content = @Content(schema = @Schema(implementation = ResponseCommentVoteDTO.class))
            ),
            @ApiResponse(
                    responseCode = "201",
                    description = "Comment vote created successfully",
                    content = @Content(schema = @Schema(implementation = ResponseCommentVoteDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data or malformed Snowflake ID",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Comment not found",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Service temporarily unavailable (Circuit Breaker active)",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            )
    })
    ResponseEntity<?> toggle(
            @Valid @RequestBody ToggleCommentVoteDTO dto,
            HttpServletRequest request
    );
}
