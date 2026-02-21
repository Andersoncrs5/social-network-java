package com.blog.writeapi.configs.startup;

import cn.hutool.core.lang.Snowflake;
import com.blog.writeapi.modules.role.models.RoleModel;
import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.user.repository.UserRepository;
import com.blog.writeapi.modules.role.service.providers.RoleService;
import com.blog.writeapi.modules.userRole.service.providers.UserRoleService;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class SuperAdmInitializr implements CommandLineRunner {

    private final UserRepository repository;
    private final RoleService roleService;
    private final UserRoleService userRoleService;
    private final Argon2PasswordEncoder encoder;
    private final Snowflake snowflake;

    @Override
    @Transactional
    @Retry(name = "super-adm-retry", fallbackMethod = "fallbackCreateSuperAdm")
    public void run(String... args) throws Exception {
        String email = "system.domain@gmail.com";

        UserModel superAdm = repository.findByEmailIgnoreCase(email)
                .orElseGet(() -> {
                    log.info("Creating initial System User...");
                    UserModel newUser = new UserModel().toBuilder()
                            .id(snowflake.nextId())
                            .name("System")
                            .username("System")
                            .email(email)
                            .password(this.encoder.encode("0123456789"))
                            .build();
                    return repository.save(newUser);
                });

        RoleModel superRole = roleService.findByName("SUPER_ADM_ROLE")
                .orElseThrow(() -> new RuntimeException("Role SUPER_ADM_ROLE not found."));

        RoleModel admRole = roleService.findByName("ADM_ROLE")
                .orElseThrow(() -> new RuntimeException("Role ADM_ROLE not found."));

        applyRoles(superAdm, superRole, admRole);
    }

    private void applyRoles(UserModel user, RoleModel... roles) {
        Arrays.stream(roles).forEach(role -> {
            boolean hasRole = this.userRoleService.existsByUserAndRole(user, role);

            if (!hasRole) {
                this.userRoleService.create(user, role);
                log.info("Role '{}' assigned to user '{}'.", role.getName(), user.getEmail());
            } else {
                log.debug("User '{}' already has the role '{}'.", user.getEmail(), role.getName());
            }
        });
    }

    public void fallbackCreateSuperAdm(String[] args, Exception e) {
        log.error("Critical failure initializing Super Admin: {}", e.getMessage());
    }
}
