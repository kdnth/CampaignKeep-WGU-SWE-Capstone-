package com.kdnth.campaignkeep.user;

public record UserResponse(
        Long userId,
        String username,
        String email
) { }
