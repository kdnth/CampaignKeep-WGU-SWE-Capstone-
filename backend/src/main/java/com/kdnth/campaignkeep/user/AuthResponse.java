package com.kdnth.campaignkeep.user;

public record AuthResponse(String token, Long userId, String username) { }
