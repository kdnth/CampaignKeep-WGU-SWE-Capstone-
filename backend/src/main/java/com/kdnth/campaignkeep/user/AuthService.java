package com.kdnth.campaignkeep.user;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    MessageResponse forgotPassword(ForgotPasswordRequest request);
    MessageResponse resetPassword(ResetPasswordRequest request);
    MessageResponse verifyEmail(VerifyEmailRequest request);
    MessageResponse sendVerificationEmail(Long userId);
}
