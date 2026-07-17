package com.kdnth.campaignkeep.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * Production mailer via Resend's HTTP API.
 * Selected when {@code resend.api-key} is set.
 */
public class ResendEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(ResendEmailService.class);
    private static final String RESEND_EMAILS_URL = "https://api.resend.com/emails";

    private final String apiKey;
    private final String fromAddress;
    private final RestClient restClient;

    public ResendEmailService(String apiKey, String fromAddress, RestClient.Builder restClientBuilder) {
        this.apiKey = apiKey;
        this.fromAddress = fromAddress;
        this.restClient = restClientBuilder.build();
    }

    @Override
    public void sendVerificationEmail(String toEmail, String verificationLink) {
        send(toEmail,
                "Verify your CampaignKeep email",
                """
                <p>Welcome to CampaignKeep.</p>
                <p><a href="%s">Verify your email</a></p>
                <p>This link expires in 24 hours. If you did not create an account, you can ignore this email.</p>
                """.formatted(verificationLink));
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        send(toEmail,
                "Reset your CampaignKeep password",
                """
                <p>We received a request to reset your CampaignKeep password.</p>
                <p><a href="%s">Reset your password</a></p>
                <p>This link expires in 30 minutes. If you did not request a reset, you can ignore this email.</p>
                """.formatted(resetLink));
    }

    private void send(String toEmail, String subject, String html) {
        try {
            restClient.post()
                    .uri(RESEND_EMAILS_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + apiKey)
                    .body(Map.of(
                            "from", fromAddress,
                            "to", List.of(toEmail),
                            "subject", subject,
                            "html", html
                    ))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("Failed to send email via Resend to {}: {}", toEmail, e.getMessage());
            throw new IllegalStateException("Failed to send email. Please try again later.");
        }
    }
}
