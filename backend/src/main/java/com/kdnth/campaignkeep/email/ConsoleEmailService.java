package com.kdnth.campaignkeep.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Local/dev mailer: logs the full link so you can open it without SMTP or Resend.
 * Selected automatically when {@code resend.api-key} is blank.
 */
public class ConsoleEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(ConsoleEmailService.class);

    @Override
    public void sendVerificationEmail(String toEmail, String verificationLink) {
        log.info("""
                ========== EMAIL (console) ==========
                To: {}
                Subject: Verify your CampaignKeep email
                Link: {}
                =====================================""", toEmail, verificationLink);
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        log.info("""
                ========== EMAIL (console) ==========
                To: {}
                Subject: Reset your CampaignKeep password
                Link: {}
                =====================================""", toEmail, resetLink);
    }
}
