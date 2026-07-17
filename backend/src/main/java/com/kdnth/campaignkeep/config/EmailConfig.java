package com.kdnth.campaignkeep.config;

import com.kdnth.campaignkeep.email.ConsoleEmailService;
import com.kdnth.campaignkeep.email.EmailService;
import com.kdnth.campaignkeep.email.ResendEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class EmailConfig {

    private static final Logger log = LoggerFactory.getLogger(EmailConfig.class);

    @Bean
    public EmailService emailService(
            @Value("${resend.api-key:}") String apiKey,
            @Value("${email.from:}") String fromAddress,
            RestClient.Builder restClientBuilder) {
        if (apiKey != null && !apiKey.isBlank()) {
            if (fromAddress == null || fromAddress.isBlank()) {
                throw new IllegalStateException(
                        "email.from must be set when resend.api-key is configured");
            }
            log.info("Email delivery: Resend");
            return new ResendEmailService(apiKey, fromAddress, restClientBuilder);
        }
        log.info("Email delivery: console (set RESEND_API_KEY to enable Resend)");
        return new ConsoleEmailService();
    }
}
