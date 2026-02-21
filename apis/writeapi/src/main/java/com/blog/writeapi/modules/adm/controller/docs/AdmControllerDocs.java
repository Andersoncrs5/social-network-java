package com.blog.writeapi.modules.adm.controller.docs;

import com.blog.writeapi.modules.adm.dto.ToggleRoleAdmDTO;
import com.blog.writeapi.modules.adm.dto.ToggleRoleDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface AdmControllerDocs {

    @Operation(summary = "Add a standard role to a user", description = "Allows SUPER_ADM and ADM to assign roles like MODERATOR or USER")
    @PostMapping("/roles/add")
    @PreAuthorize("hasAnyAuthority('SUPER_ADM_ROLE', 'ADM_ROLE')")
    ResponseEntity<?> addRoleToUser(
            @RequestBody @Valid ToggleRoleDTO dto,
            HttpServletRequest request
    );

    @Operation(summary = "Remove a standard role from a user")
    @PostMapping("/roles/remove")
    @PreAuthorize("hasAnyAuthority('SUPER_ADM_ROLE', 'ADM_ROLE')")
    ResponseEntity<?> removeRoleToUser(
            @RequestBody @Valid ToggleRoleDTO dto,
            HttpServletRequest request
    );

    @Operation(summary = "Toggle Administrative role", description = "Only accessible by SUPER_ADM. Adds or removes ADM_ROLE")
    @PatchMapping("/toggle/role/adm")
    @PreAuthorize("hasAnyAuthority('SUPER_ADM_ROLE')")
    ResponseEntity<?> toggleRoleAdmInUser(
            @RequestBody @Valid ToggleRoleAdmDTO dto,
            HttpServletRequest request
    );
}