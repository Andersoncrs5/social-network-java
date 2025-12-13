package com.blog.writeapi.controllers.docs;

import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.res.classes.user.ResponseUserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

public interface UserControllerDocs {

    @GetMapping("/me")
    @Operation(summary = "Get user", tags = {"User"})
    @ApiResponse(responseCode = "404",
            description = "User not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseHttp.class)))
    @ApiResponse(responseCode = "200",
            description = "User found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseUserDTO.class)))
    ResponseEntity<?> getUser(HttpServletRequest request);

    @DeleteMapping
    @Operation(summary = "Delete user", tags = {"User"})
    @ApiResponse(responseCode = "404",
            description = "User not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseHttp.class)))
    @ApiResponse(responseCode = "200",
            description = "User deleted",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseHttp.class)))
    ResponseEntity<?> deleteUser(HttpServletRequest request);

}
