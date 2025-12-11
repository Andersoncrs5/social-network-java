package com.blog.writeapi.services.providers;

import com.blog.writeapi.models.RoleModel;
import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.services.interfaces.ITokenService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TokenService implements ITokenService {

    @Value("${spring.security.jwt.secret}")
    private String secret;
    @Value("${spring.security.exp.token}")
    private int expToken;
    @Value("${spring.security.exp.refresh}")
    private int expRefreshToken;

    @Override
    public String generateToken(UserModel user, List<RoleModel> roles) {
        if (secret.isBlank()) {
            log.error("Error! The secret is null! in class TokenService");
            throw new RuntimeException();
        }

        List<String> roleNames = roles.stream()
                .map(RoleModel::getName)
                .collect(Collectors.toList());

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("name", user.getName())
                .claim("username", user.getUsername())
                .claim("email", user.getEmail())
                .issueTime(Date.from(Instant.now()))
                .expirationTime(Date.from(this.genExpirationDate()))
                .claim("roles", roleNames)
                .jwtID(UUID.randomUUID().toString())
                .build();

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        SignedJWT signedJWT = new SignedJWT(header, jwtClaimsSet);

        try {
            signedJWT.sign(new MACSigner(secret.getBytes()));
            return signedJWT.serialize();
        } catch (KeyLengthException e) {
            log.error("Error the assign token! Error: {}", e.getMessage());
            throw new RuntimeException(e);
        } catch (JOSEException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao assinar o refresh token.");
        }
    }

    private Instant genExpirationDate() {
        return LocalDateTime.now().plusHours(expToken).toInstant(ZoneOffset.of("-03:00"));
    }

    private Instant genExpirationDateRefreshToken() {
        return LocalDateTime.now().plusHours(expRefreshToken).toInstant(ZoneOffset.of("-03:00"));
    }

    @Override
    public String validateToken(String token) {
        log.debug("Validating token...");
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        if (secret == null || secret.isBlank()) {
            throw new RuntimeException("Secret key not set up");
        }

        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            MACVerifier verifier = new MACVerifier(secret.getBytes());

            if (!signedJWT.verify(verifier)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
            }

            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            if (claimsSet.getExpirationTime().before(new Date())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
            }

            return claimsSet.getSubject();
        } catch (ParseException | JOSEException e) {
            log.debug("Error the to parse or to check the token: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public Map<String, Object> extractAllClaims(String token) {
        log.debug("Extraindo todas as claims do token.");
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claimSet = signedJWT.getJWTClaimsSet();
            log.debug("Claims extracted.");
            return claimSet.getClaims();
        } catch (ParseException e) {
            log.error("Erro ao extrair claims do token: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token mal formatado.", e);
        }
    }

    @Override
    public String extractSubjectFromRequest(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        return getClaimsFromToken(token).getSubject();
    }

    @Override
    public String extractUserIdFromRequest(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        return getClaimsFromToken(token).getClaim("userId").toString();
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("Cabeçalho de autorização não encontrado ou formato inválido.");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return authHeader.substring(7);
    }

    private JWTClaimsSet getClaimsFromToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet();
        } catch (ParseException e) {
            log.error("Erro ao parsear o token para extrair claims: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido.", e);
        }
    }

}
