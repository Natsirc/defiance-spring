package com.defiance.defiance.service;

import com.defiance.defiance.dto.ContactRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    private static final Logger logger = LoggerFactory.getLogger(MailService.class);
    private final JavaMailSender mailSender;
    private final String fromAddress;

    public MailService(JavaMailSender mailSender, @Value("${spring.mail.from}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    public void sendVerificationEmail(String to, String link) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom(fromAddress);
        message.setSubject("DefianceCo Email Verification");
        message.setText("Please verify your email by clicking the link: " + link);
        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom(fromAddress);
        message.setSubject("DefianceCo Password Reset");
        message.setText("Use this token to reset your password: " + token);
        mailSender.send(message);
    }

    public void sendContactEmail(String to, ContactRequest request) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom(fromAddress);
        String subject = request.getSubject();
        if (subject == null || subject.trim().isEmpty()) {
            subject = "General Inquiry";
        }
        message.setSubject("DefianceCo Contact: " + subject);
        message.setReplyTo(request.getEmail());
        message.setText(
            "From: " + request.getName() + " <" + request.getEmail() + ">\n\n"
                + request.getMessage()
        );
        try {
            mailSender.send(message);
        } catch (Exception ex) {
            logger.error("Contact email send failed: {}", ex.getMessage(), ex);
            throw ex;
        }
    }
}
