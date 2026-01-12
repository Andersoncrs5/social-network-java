package com.blog.writeapi.controllers.docs;

import com.blog.writeapi.dtos.postVote.TogglePostVoteDTO;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.res.swagger.postFavorite.ResponsePostFavoriteDTO;
import com.blog.writeapi.utils.res.swagger.postVote.ResponsePostVoteDTO;
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

@Tag(name = "PostVote", description = "Endpoints for managing vote to post")
public interface PostVoteControllerDocs {

    @PostMapping
    @Operation(
            summary = "Toggle post vote (Upvote/Downvote)",
            description = """
                    Handles the voting logic for a post. If the same vote type is sent, it removes the vote.
                    If a different type is sent, it updates the vote. If no vote exists, it creates one.
                    """
    )
    @CircuitBreaker(name = "tag-toggle-cb")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Vote removed or updated successfully",
                    content = @Content(schema = @Schema(implementation = ResponsePostVoteDTO.class))
            ),
            @ApiResponse(
                    responseCode = "201",
                    description = "Vote created successfully",
                    content = @Content(schema = @Schema(implementation = ResponsePostVoteDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data or malformed Snowflake ID",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Post not found",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Service temporarily unavailable (Circuit Breaker active)",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            )
    })
    ResponseEntity<?> toggle(
            @Valid @RequestBody TogglePostVoteDTO dto,
            HttpServletRequest request
    );

}
