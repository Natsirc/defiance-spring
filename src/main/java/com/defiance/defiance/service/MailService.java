package com.defiance.defiance.service;

import com.defiance.defiance.dto.ContactRequest;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MailService {
    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    private final RestTemplate restTemplate;
    private final String fromAddress;
    private final String brevoApiKey;
    private final String brevoApiBaseUrl;

    public MailService(
        RestTemplateBuilder restTemplateBuilder,
        @Value("${spring.mail.from}") String fromAddress,
        @Value("${brevo.api.key}") String brevoApiKey,
        @Value("${brevo.api.base-url:https://api.brevo.com}") String brevoApiBaseUrl
    ) {
        this.restTemplate = restTemplateBuilder
            .setConnectTimeout(Duration.ofSeconds(5))
            .setReadTimeout(Duration.ofSeconds(10))
            .build();
        this.fromAddress = fromAddress;
        this.brevoApiKey = brevoApiKey;
        this.brevoApiBaseUrl = brevoApiBaseUrl;
    }

    public void sendVerificationEmail(String to, String link) {
        sendBrevoEmail(
            to,
            "DefianceCo Email Verification",
            "Please verify your email by clicking the link: " + link,
            null
        );
    }

    public void sendPasswordResetEmail(String to, String token) {
        sendBrevoEmail(
            to,
            "DefianceCo Password Reset",
            "Use this token to reset your password: " + token,
            null
        );
    }

    public void sendContactEmail(String to, ContactRequest request) {
        String subject = request.getSubject();
        if (subject == null || subject.trim().isEmpty()) {
            subject = "General Inquiry";
        }
        String body = "From: " + request.getName() + " <" + request.getEmail() + ">\n\n"
            + request.getMessage();
        Map<String, String> replyTo = new HashMap<>();
        replyTo.put("email", request.getEmail());
        replyTo.put("name", request.getName());
        sendBrevoEmail(to, "DefianceCo Contact: " + subject, body, replyTo);
    }

    private void sendBrevoEmail(
        String to,
        String subject,
        String textContent,
        Map<String, String> replyTo
    ) {
        if (brevoApiKey == null || brevoApiKey.trim().isEmpty()) {
            throw new IllegalStateException("Brevo API key is not configured.");
        }

        Map<String, String> from = parseFromAddress(fromAddress);
        Map<String, Object> payload = new HashMap<>();
        payload.put("sender", from);
        payload.put("to", List.of(Map.of("email", to)));
        payload.put("subject", subject);
        payload.put("textContent", textContent);
        if (replyTo != null) {
            payload.put("replyTo", replyTo);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", brevoApiKey);

        String url = brevoApiBaseUrl + "/v3/smtp/email";
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                url,
                new HttpEntity<>(payload, headers),
                String.class
            );
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Brevo API error: " + response.getStatusCode());
            }
        } catch (Exception ex) {
            logger.error("Contact email send failed: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    private Map<String, String> parseFromAddress(String value) {
        Map<String, String> sender = new HashMap<>();
        if (value == null || value.trim().isEmpty()) {
            sender.put("email", "no-reply@example.com");
            sender.put("name", "DefianceCo");
            return sender;
        }

        int lt = value.indexOf('<');
        int gt = value.indexOf('>');
        if (lt >= 0 && gt > lt) {
            String name = value.substring(0, lt).trim();
            String email = value.substring(lt + 1, gt).trim();
            sender.put("email", email);
            sender.put("name", name.isEmpty() ? email : name);
            return sender;
        }

        sender.put("email", value.trim());
        sender.put("name", "DefianceCo");
        return sender;
    }
}
