package com.blog.writeapi.configs.security;

import com.blog.writeapi.modules.user.models.UserModel;
import com.blog.writeapi.modules.userRole.models.UserRoleModel;
import com.blog.writeapi.modules.user.repository.UserRepository;
import com.blog.writeapi.modules.userRole.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String PREFIX = "auth:user:";

    @Override
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        String key = PREFIX + email;

        try {
            Object cached = redisTemplate.opsForValue().get(key);

            if (cached != null) {
                return (UserPrincipal) cached;
            }
        } catch (Exception e) {
            log.warn("Error reading cache from Redis to {}: {}", email, e.getMessage());
        }

        UserModel user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<UserRoleModel> roles = this.userRoleRepository.findAllByUser(user);

        if (user.getLoginBlockAt() != null && user.getLoginBlockAt().isAfter(OffsetDateTime.now())) {
            throw new DisabledException("User account is temporarily blocked until " + user.getLoginBlockAt());
        }

        UserPrincipal principal = new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                roles.stream()
                        .map(role -> new SimpleGrantedAuthority(role.getRole().getName()))
                        .toList(),
                user
        );

        try {
            redisTemplate.opsForValue().set(key, principal, Duration.ofMinutes(10)); // 👈 direto
        } catch (Exception e) {
            log.error("Error saving to Redis: {}", e.getMessage());
        }

        return principal;
    }

//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        UserModel user = userRepository.findByEmailIgnoreCase(email)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//        List<UserRoleModel> roles = this.userRoleRepository.findAllByUser(user);
//
//        if (user.getLoginBlockAt() != null && user.getLoginBlockAt().isAfter(OffsetDateTime.now())) {
//            throw new DisabledException("User account is temporarily blocked until " + user.getLoginBlockAt());
//        }
//
//        return new UserPrincipal(
//                user.getId(),
//                user.getEmail(),
//                user.getPassword(),
//                roles.stream()
//                        .map(role -> new SimpleGrantedAuthority(role.getRole().getName()))
//                        .toList()
//        );
//    }
}
