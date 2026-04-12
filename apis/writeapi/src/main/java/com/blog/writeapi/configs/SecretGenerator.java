package com.blog.writeapi.configs;

import java.security.SecureRandom;
import java.util.Base64;

public class SecretGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();

    public static String generate(String prefix) {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        String token = base64Encoder.encodeToString(randomBytes);

        return prefix + "_" + token;
    }
}