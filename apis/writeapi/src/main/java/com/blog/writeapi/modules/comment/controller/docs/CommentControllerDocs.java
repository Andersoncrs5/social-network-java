package com.blog.writeapi.modules.comment.controller.docs;

import com.blog.writeapi.modules.comment.dtos.CreateCommentDTO;
import com.blog.writeapi.modules.comment.dtos.UpdateCommentDTO;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.res.swagger.comment.ResponseCommentDTO;
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
import org.springframework.web.bind.annotation.*;

@Tag(name = "Comment", description = "Endpoints for managing post comments")
public interface CommentControllerDocs {

    @PatchMapping("/{id}")
    @CircuitBreaker(name = "tagUpdateCB")
    @Operation(
            summary = "Update an existing comment",
            description = "Updates the content or status of a comment. Only the original author is allowed to perform this action."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Comment updated successfully",
                    content = @Content(schema = @Schema(implementation = ResponseCommentDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data or malformed Snowflake ID",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied: You are not the author of this comment",
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
    ResponseEntity<?> update(
            @Parameter(description = "The unique Snowflake ID of the comment", example = "1998780200074176609")
            @PathVariable @IsId Long id,
            @Valid @RequestBody UpdateCommentDTO dto,
            HttpServletRequest request
    );

    @PostMapping
    @CircuitBreaker(name = "tagCreateCB")
    @Operation(
            summary = "Create a new comment",
            description = "Creates a comment associated with a specific post. It can be a top-level comment or a reply to an existing one if parentId is provided."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Comment created successfully",
                    content = @Content(schema = @Schema(implementation = ResponseCommentDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data (Validation Error)",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Post or Parent Comment not found",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Service temporarily unavailable (Circuit Breaker active)",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            )
    })
    ResponseEntity<?> create(
            @Valid @RequestBody CreateCommentDTO dto,
            HttpServletRequest request
    );

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a comment",
            description = "Removes a comment from the database by its Snowflake ID. Requires ownership or admin privileges."
    )
    @CircuitBreaker(name = "tagDeleteCB")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Comment deleted successfully",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Comment not found",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Operation not permitted",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid ID format",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Service temporarily unavailable (Circuit Breaker active)",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            )
    })
    ResponseEntity<?> del(
            @Parameter(description = "The Snowflake ID of the comment", example = "1998780200074176609")
            @PathVariable @IsId Long id,
            HttpServletRequest request
    );

}
