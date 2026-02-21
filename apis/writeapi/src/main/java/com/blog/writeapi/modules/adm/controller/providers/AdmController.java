package com.blog.writeapi.modules.adm.controller.providers;

import com.blog.writeapi.modules.adm.controller.docs.AdmControllerDocs;
import com.blog.writeapi.modules.adm.dto.ToggleRoleAdmDTO;
import com.blog.writeapi.modules.adm.dto.ToggleRoleDTO;
import com.blog.writeapi.modules.role.models.RoleModel;
import com.blog.writeapi.modules.role.service.docs.IRoleService;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.user.service.docs.IUserService;
import com.blog.writeapi.modules.userRole.models.UserRoleModel;
import com.blog.writeapi.modules.userRole.service.docs.IUserRoleService;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.services.interfaces.ITokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/adm")
public class AdmController implements AdmControllerDocs {

    private final IUserRoleService userRoleService;
    private final IUserService userService;
    private final IRoleService roleService;
    private final ITokenService tokenService;

    @Override
    public ResponseEntity<?> addRoleToUser(
            @RequestBody @Valid ToggleRoleDTO dto,
            HttpServletRequest request
    ) {
        RoleModel role = roleService.findByNameSimple(dto.roleName());
        UserModel user = this.userService.GetByIdSimple(dto.userId());

        if (
                role.getName().equals("SUPER_ADM_ROLE") ||
                role.getName().equals("ADM_ROLE")
        ) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseHttp<>(
                    null,
                    "You have not permission to add role " + role.getName(),
                    UUID.randomUUID().toString(),
                    1,
                    false,
                    OffsetDateTime.now()
            ));
        }

        if (this.userRoleService.existsByUserAndRole(user, role))
        {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseHttp<>(
                    null,
                    "Role: "  + role.getName() + " already added to user: " + user.getName() ,
                    UUID.randomUUID().toString(),
                    1,
                    false,
                    OffsetDateTime.now()
            ));
        }

        this.userRoleService.create(user, role);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseHttp<>(
                null,
                "Role: " + role.getName() + " added to user " + user.getName(),
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        ));
    }

    @Override
    public ResponseEntity<?> removeRoleToUser(
            @RequestBody @Valid ToggleRoleDTO dto,
            HttpServletRequest request
    ) {
        RoleModel role = roleService.findByNameSimple(dto.roleName());
        UserModel user = this.userService.GetByIdSimple(dto.userId());

        if (
                role.getName().equals("SUPER_ADM_ROLE") ||
                        role.getName().equals("ADM_ROLE")
        ) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseHttp<>(
                    null,
                    "You have not permission to add role " + role.getName(),
                    UUID.randomUUID().toString(),
                    1,
                    false,
                    OffsetDateTime.now()
            ));
        }

        Optional<UserRoleModel> modelOptional = this.userRoleService.getByUserAndRole(user, role);

        if (modelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseHttp<>(
                    null,
                    "User: "  + user.getUsername() + " have not role: " + role.getName(),
                    UUID.randomUUID().toString(),
                    1,
                    false,
                    OffsetDateTime.now()
            ));
        }

        this.userRoleService.delete(modelOptional.get());

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHttp<>(
                null,
                "Role: "  + role.getName() + " removed of user: " + user.getName() ,
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        ));
    }

    @Override
    @Transactional
    public ResponseEntity<?> toggleRoleAdmInUser(
            @RequestBody @Valid ToggleRoleAdmDTO dto,
            HttpServletRequest request
    ) {
        Long userId = this.tokenService.extractUserIdFromRequest(request);

        RoleModel role = this.roleService.findByNameSimple("ADM_ROLE");
        UserModel user = this.userService.GetByIdSimple(dto.userId());

        if (Objects.equals(userId, user.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseHttp<>(
                    null,
                    "You cannot toggle your own administrative role." ,
                    UUID.randomUUID().toString(),
                    1,
                    false,
                    OffsetDateTime.now()
            ));
        }

        Optional<UserRoleModel> optional = this.userRoleService.getByUserAndRole(user, role);

        if (optional.isPresent()) {
            this.userRoleService.delete(optional.get());

            return ResponseEntity.status(HttpStatus.OK).body(new ResponseHttp<>(
                    null,
                    "Role: "  + role.getName() + " removed to user: " + user.getName() ,
                    UUID.randomUUID().toString(),
                    1,
                    true,
                    OffsetDateTime.now()
            ));
        }

        this.userRoleService.create(user, role);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseHttp<>(
                null,
                "Role: "  + role.getName() + " added to user: " + user.getName() ,
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        ));
    }

}
