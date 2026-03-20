package com.blog.writeapi.configs.api.metadata;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

@Service
public class HttpRequestUtils {

    public ClientMetadataDTO extractMetadata(HttpServletRequest request) {
        String ip = extractIp(request);
        String ua = request.getHeader("User-Agent");

        String fingerprint = DigestUtils.sha256Hex(ip + (ua != null ? ua : ""));

        return new ClientMetadataDTO(ip, ua, fingerprint);
    }

    private String extractIp(HttpServletRequest request) {
        String remoteAdder = request.getHeader("X-Forwarded-For");
        if (remoteAdder == null || remoteAdder.isEmpty()) {
            remoteAdder = request.getRemoteAddr();
        }
        return remoteAdder;
    }

    public String generateFingerprint(String ip, String ua) {
        return DigestUtils.sha256Hex(ip + ua);
    }
}