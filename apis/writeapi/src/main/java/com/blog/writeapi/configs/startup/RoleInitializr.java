package com.blog.writeapi.configs.startup;

import com.blog.writeapi.modules.role.models.RoleModel;
import com.blog.writeapi.modules.role.service.providers.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class RoleInitializr implements CommandLineRunner {

    private final RoleService service;

    @Override
    public void run(String... args) throws Exception {
        createRole("USER_ROLE");
        createRole("ADM_ROLE");
        createRole("SUPER_ADM_ROLE");
        createRole("MODERATOR_ROLE");
    }

    void createRole(String name) {
        Boolean exists = this.service.existsByName(name);

        if (exists) {
            log.debug("Role with name {} already exists", name);
            return;
        }

        RoleModel role = new RoleModel().toBuilder()
                .name(name.toUpperCase())
                .build();

        RoleModel model = this.service.create(role);
        log.debug("Role {} created!", model.getName());

    }

}
