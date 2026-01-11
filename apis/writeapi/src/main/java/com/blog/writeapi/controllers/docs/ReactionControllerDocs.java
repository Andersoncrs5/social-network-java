package com.blog.writeapi.controllers.docs;

import com.blog.writeapi.dtos.reaction.CreateReactionDTO;
import com.blog.writeapi.dtos.reaction.UpdateReactionDTO;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.res.swagger.reaction.ResponseReactionDTO;
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

@Tag(name = "Reaction", description = "Endpoints for managing reaction")
public interface ReactionControllerDocs {

    @PostMapping
    @CircuitBreaker(name = "tagCreateCB")
    @Operation(
            summary = "Create a new reaction",
            description = "Registers a new reaction type in the system catalog. This is typically an administrative operation."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "reaction created successfully",
                    content = @Content(schema = @Schema(implementation = ResponseReactionDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data (Validation Error)",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Service temporarily unavailable (Circuit Breaker active)",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            )
    })
    ResponseEntity<?> create(
            @Valid @RequestBody CreateReactionDTO dto,
            HttpServletRequest request
    );

    @DeleteMapping("/{id}")
    @CircuitBreaker(name = "tagDeleteCB")
    @Operation(
            summary = "Delete a reaction type",
            description = "Removes a reaction type from the system catalog by its ID.",
            method = "DELETE"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reaction deleted successfully",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid ID provided (Validation Error)",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Reaction not found",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Service temporarily unavailable (Circuit Breaker active)",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            )
    })
    ResponseEntity<?> delete(
            @Parameter(description = "The Snowflake ID of the comment", example = "1998780200074176609")
            @IsId @PathVariable Long id,
            HttpServletRequest request
    );

    @PatchMapping("/{id}")
    @CircuitBreaker(name = "tagUpdateCB")
    @Operation(
            summary = "Update an existing reaction",
            description = "Updates specific fields of an existing reaction type. Only provided fields will be modified."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reaction updated successfully",
                    content = @Content(schema = @Schema(implementation = ResponseReactionDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or ID (Validation Error)",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Reaction not found",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error internal in server",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Service temporarily unavailable (Circuit Breaker active)",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            )
    })
    ResponseEntity<?> update(
            @Parameter(description = "The Snowflake ID of the reaction", example = "1998780200074176609")
            @IsId @PathVariable Long id,
            @Valid @RequestBody UpdateReactionDTO dto,
            HttpServletRequest request
    );

}
