package com.blog.writeapi.controllers.docs;

import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.res.swagger.commentFavorite.ResponseCommentFavorite;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Tag(name = "CommentFavorite", description = "Endpoints for managing comment favorite")
public interface CommentFavoriteControllerDocs {

    @PostMapping("/{commentID}/toggle")
    @CircuitBreaker(name = "tagToggleCB")
    @Operation(
            summary = "Toggle comment favorite status",
            description = "Adds a comment to the user's favorites or removes it if it's already favored."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Comment added to favorites",
                    content = @Content(schema = @Schema(implementation = ResponseCommentFavorite.class))
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "Comment removed from favorites",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Comment not found",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input data or Snowflake ID format",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized: Authentication token is missing or invalid",
                    content = @Content(mediaType = "application/json"))
    })
    ResponseEntity<?> toggle(
            @Parameter(description = "The ID of the comment", example = "1998780200074176609")
            @PathVariable @IsId Long commentID,
            HttpServletRequest request
    );
}
