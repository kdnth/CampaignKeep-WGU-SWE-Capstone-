package com.kdnth.campaignkeep.user;

import com.kdnth.campaignkeep.email.EmailService;
import com.kdnth.campaignkeep.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private static final Duration VERIFICATION_TTL = Duration.ofHours(24);
    private static final Duration RESET_TTL = Duration.ofMinutes(30);
    private static final String FORGOT_PASSWORD_MESSAGE =
            "If an account exists for that email, we sent a password reset link.";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AccountTokenService accountTokenService;
    private final EmailService emailService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username already exists. Please choose a different username.");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        userRepository.save(user);

        try {
            sendVerificationToUser(user);
        } catch (Exception e) {
            // Registration must succeed even if email delivery fails; banner resend is the recovery path.
            log.warn("Failed to send verification email on register for user {}: {}",
                    user.getId(), e.getMessage());
        }

        String token = jwtUtil.generateToken(user.getId());
        return new AuthResponse(token, user.getId(), user.getUsername());
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.identifier(), request.password())
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
        User user = userRepository.findByUsername(request.identifier())
                .or(() -> userRepository.findByEmail(request.identifier()))
                .orElseThrow(() -> new IllegalStateException("User not found"));

        String token = jwtUtil.generateToken(user.getId());
        return new AuthResponse(token, user.getId(), user.getUsername());
    }

    @Override
    @Transactional
    public MessageResponse forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmail(request.email()).ifPresent(user -> {
            try {
                String rawToken = accountTokenService.issue(user, AccountTokenType.PASSWORD_RESET, RESET_TTL);
                String link = frontendUrl + "/reset-password?token=" + rawToken;
                emailService.sendPasswordResetEmail(user.getEmail(), link);
            } catch (Exception e) {
                // Same response either way — do not leak whether the email exists or delivery failed.
                log.warn("Password reset email failed for user {}: {}", user.getId(), e.getMessage());
            }
        });
        return new MessageResponse(FORGOT_PASSWORD_MESSAGE);
    }

    @Override
    @Transactional
    public MessageResponse resetPassword(ResetPasswordRequest request) {
        User user = accountTokenService.consume(request.token(), AccountTokenType.PASSWORD_RESET);
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        return new MessageResponse("Password updated. You can sign in with your new password.");
    }

    @Override
    @Transactional
    public MessageResponse verifyEmail(VerifyEmailRequest request) {
        User user = accountTokenService.consumeEmailVerification(request.token());
        if (user.getEmailVerifiedAt() == null) {
            user.setEmailVerifiedAt(LocalDateTime.now());
            userRepository.save(user);
        }
        return new MessageResponse("Email verified successfully.");
    }

    @Override
    @Transactional
    public MessageResponse sendVerificationEmail(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.isEmailVerified()) {
            return new MessageResponse("Email is already verified.");
        }

        sendVerificationToUser(user);
        return new MessageResponse("Verification email sent.");
    }

    private void sendVerificationToUser(User user) {
        String rawToken = accountTokenService.issue(
                user, AccountTokenType.EMAIL_VERIFICATION, VERIFICATION_TTL);
        String link = frontendUrl + "/verify-email?token=" + rawToken;
        emailService.sendVerificationEmail(user.getEmail(), link);
    }
}
