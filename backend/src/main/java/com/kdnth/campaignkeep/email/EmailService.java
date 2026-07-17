package com.kdnth.campaignkeep.email;

public interface EmailService {

    void sendVerificationEmail(String toEmail, String verificationLink);

    void sendPasswordResetEmail(String toEmail, String resetLink);
}
