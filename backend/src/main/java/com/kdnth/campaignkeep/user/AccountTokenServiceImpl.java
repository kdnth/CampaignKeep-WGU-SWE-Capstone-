package com.kdnth.campaignkeep.user;

import com.kdnth.campaignkeep.base.TooManyRequestsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountTokenServiceImpl implements AccountTokenService {

    private static final Duration ISSUE_COOLDOWN = Duration.ofSeconds(60);

    private final AccountTokenRepository accountTokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional
    public String issue(User user, AccountTokenType type, Duration ttl) {
        accountTokenRepository.findFirstByUserAndTypeOrderByCreatedOnDesc(user, type)
                .ifPresent(latest -> {
                    if (latest.getCreatedOn() != null
                            && latest.getCreatedOn().isAfter(LocalDateTime.now().minus(ISSUE_COOLDOWN))) {
                        throw new TooManyRequestsException(
                                "Please wait a minute before requesting another email.");
                    }
                });

        List<AccountToken> active = accountTokenRepository.findByUserAndTypeAndConsumedAtIsNull(user, type);
        LocalDateTime now = LocalDateTime.now();
        for (AccountToken existing : active) {
            existing.setConsumedAt(now);
        }
        if (!active.isEmpty()) {
            accountTokenRepository.saveAll(active);
        }

        String rawToken = generateRawToken();

        AccountToken token = new AccountToken();
        token.setUser(user);
        token.setType(type);
        token.setTokenHash(hashToken(rawToken));
        token.setExpiresAt(now.plus(ttl));
        accountTokenRepository.save(token);

        return rawToken;
    }

    @Override
    @Transactional
    public User consume(String rawToken, AccountTokenType type) {
        AccountToken token = findValidToken(rawToken, type);
        token.setConsumedAt(LocalDateTime.now());
        accountTokenRepository.save(token);
        return token.getUser();
    }

    @Override
    @Transactional
    public User consumeEmailVerification(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new IllegalArgumentException("Invalid or expired token.");
        }

        AccountToken token = accountTokenRepository
                .findByTokenHashAndType(hashToken(rawToken), AccountTokenType.EMAIL_VERIFICATION)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token."));

        User user = token.getUser();
        if (token.getConsumedAt() != null) {
            if (user.isEmailVerified()) {
                return user;
            }
            throw new IllegalArgumentException("Invalid or expired token.");
        }
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Invalid or expired token.");
        }

        token.setConsumedAt(LocalDateTime.now());
        accountTokenRepository.save(token);
        return user;
    }

    private AccountToken findValidToken(String rawToken, AccountTokenType type) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new IllegalArgumentException("Invalid or expired token.");
        }

        AccountToken token = accountTokenRepository.findByTokenHashAndType(hashToken(rawToken), type)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token."));

        if (token.getConsumedAt() != null) {
            throw new IllegalArgumentException("Invalid or expired token.");
        }
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Invalid or expired token.");
        }
        return token;
    }

    private String generateRawToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    static String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
