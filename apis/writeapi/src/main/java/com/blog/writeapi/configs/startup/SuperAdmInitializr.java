package com.blog.writeapi.configs.startup;

import com.blog.writeapi.models.RoleModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.repositories.UserRepository;
import com.blog.writeapi.services.providers.RoleService;
import com.blog.writeapi.services.providers.UserRoleService;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class SuperAdmInitializr implements CommandLineRunner {

    private final UserRepository repository;
    private final RoleService roleRepository;
    private final UserRoleService userRoleService;
    private final Argon2PasswordEncoder encoder;

    @Override
    @Transactional
    @Retry(name = "super-adm-retry", fallbackMethod = "fallbackCreateCategory")
    public void run(String... args) throws Exception {
        String email = "system.domain@gmail.com";

        UserModel superAdm = repository.findByEmailIgnoreCase(email)
                .orElseGet(() -> {
                    UserModel newUser = new UserModel().toBuilder()
                            .name("System")
                            .username("System")
                            .email(email)
                            .password(this.encoder.encode("0123456789"))
                            .build();
                    newUser.setId(new Random().nextLong(1, Long.MAX_VALUE));
                    return repository.save(newUser);
                });

        RoleModel role = roleRepository.findByName("SUPER_ADM_ROLE")
                .orElseThrow(() -> new RuntimeException("Role SUPER_ADM_ROLE not found yet."));
        applyRole(superAdm, role);
    }

    private void applyRole(UserModel user, RoleModel role) {
        if (user == null || role == null) return;

        boolean hasRole = this.userRoleService.existsByUserAndRole(user, role);

        if (hasRole) {
            log.debug("User '{}' already has the role '{}'.", user.getEmail(), role.getName());
            return;
        }

        this.userRoleService.create(user, role);
        log.debug("Role '{}' assigned to user '{}'.", role.getName(), user.getEmail());
    }

    public void fallbackCreateCategory(String[] args, Exception e) {
        log.error("All attempts to create a category have failed.: {}", e.getMessage());
    }

}
