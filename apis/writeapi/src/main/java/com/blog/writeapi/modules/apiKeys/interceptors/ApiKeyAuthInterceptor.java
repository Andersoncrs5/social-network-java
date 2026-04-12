package com.blog.writeapi.modules.apiKeys.interceptors;

import com.blog.writeapi.modules.apiKeys.repository.ApiKeyRepository;
import com.blog.writeapi.modules.apiKeys.service.interfaces.IApiKeyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class ApiKeyAuthInterceptor implements HandlerInterceptor {

    private final ApiKeyRepository repository;
    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        String apiKey = request.getHeader("X-API-KEY");

        if (apiKey == null || apiKey.isBlank()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "API Key is missing");
            return false;
        }

        String requestHash = org.apache.commons.codec.digest.DigestUtils.sha256Hex(apiKey);

        boolean isValid = repository.findByKeyHashAndActiveTrue(requestHash).isPresent();

        if (!isValid) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API Key");
            return false;
        }

        return true;
    }
    private final IApiKeyService service;
}
