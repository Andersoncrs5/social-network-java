package com.blog.writeapi.configs.api.idempotent;

import com.blog.writeapi.utils.exceptions.BusinessRuleException;
import com.blog.writeapi.utils.exceptions.ConflictRuleException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class IdempotencyAspect {

    private final StringRedisTemplate redisTemplate;
    private final HttpServletRequest request;

    @Around("@annotation(idempotent)")
    public Object execute(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        String key = request.getHeader("X-Idempotency-Key");

        if (key == null || key.isBlank()) {
            throw new BusinessRuleException("Header X-Idempotency-Key is required for this operation.");
        }

        Boolean success = redisTemplate.opsForValue().setIfAbsent(
                "idempotency:" + key,
                "locked",
                Duration.ofSeconds(idempotent.expire())
        );

        if (Boolean.FALSE.equals(success)) {
            log.warn("Requisição duplicada detectada para a chave: {}", key);
            throw new ConflictRuleException("Duplicate request detected. Please wait.");
        }

        return joinPoint.proceed();
    }
}
