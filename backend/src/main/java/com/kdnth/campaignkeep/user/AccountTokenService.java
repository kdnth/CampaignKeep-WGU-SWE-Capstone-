package com.kdnth.campaignkeep.user;

public interface AccountTokenService {

    /**
     * Creates a new token for the user, invalidating any prior unconsumed tokens of the same type.
     * Returns the raw token to embed in an email link — never persist or log the raw value beyond that.
     */
    String issue(User user, AccountTokenType type, java.time.Duration ttl);

    /**
     * Validates and consumes a raw token. Throws IllegalArgumentException if missing, expired, or already used.
     */
    User consume(String rawToken, AccountTokenType type);

    /**
     * Like {@link #consume}, but if the token was already consumed and the user is verified,
     * returns that user so a double-clicked verification link stays idempotent.
     */
    User consumeEmailVerification(String rawToken);
}
