package com.kdnth.campaignkeep.security;

import com.kdnth.campaignkeep.user.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private String jstExpiration;

    private final UserRepository userRepository;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Long userId) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(Long.parseLong(jstExpiration));
        return Jwts.builder()
                   .subject(userId.toString())
                   .issuedAt(Date.from(now))
                   .expiration(Date.from(expiry))
                   .signWith(key)
                   .compact();
    }

    public Long getUserIdFromToken(String token) {
        String subject = Jwts.parser().verifyWith(key).build()
                             .parseSignedClaims(token)
                             .getPayload()
                             .getSubject();
        return Long.parseLong(subject);
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.error("Invalid JWT Token");
        }
        return false;
    }

}
