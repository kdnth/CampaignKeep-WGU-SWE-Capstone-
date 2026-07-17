package com.kdnth.campaignkeep.user;

public record UserResponse(
        Long userId,
        String username,
        String email,
        boolean emailVerified
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.isEmailVerified()
        );
    }
}
