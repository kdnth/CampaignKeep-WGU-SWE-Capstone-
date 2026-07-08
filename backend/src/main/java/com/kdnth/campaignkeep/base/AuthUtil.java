package com.kdnth.campaignkeep.base;

import org.springframework.security.core.Authentication;

public final class AuthUtil {
    private AuthUtil() {}

    public static Long  getUserId(Authentication authentication) {
        return (Long) authentication.getPrincipal();
    }
}
