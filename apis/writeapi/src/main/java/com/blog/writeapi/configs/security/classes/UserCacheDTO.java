package com.blog.writeapi.configs.security.classes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCacheDTO implements Serializable {
    private Long id;
    private String email;
    private String password;
    private String username;
    private List<String> roles;
    private OffsetDateTime loginBlockAt;
}
