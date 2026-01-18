package com.defiance.defiance.controller;

import com.defiance.defiance.dto.ContactRequest;
import com.defiance.defiance.service.MailService;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ContactController {
    private final MailService mailService;
    private final String contactEmail;

    public ContactController(MailService mailService, @Value("${app.contact-email}") String contactEmail) {
        this.mailService = mailService;
        this.contactEmail = contactEmail;
    }

    @PostMapping("/contact")
    public ResponseEntity<Map<String, Object>> contact(@Valid @RequestBody ContactRequest request) {
        Map<String, Object> body = new HashMap<>();
        try {
            mailService.sendContactEmail(contactEmail, request);
            body.put("success", true);
            return ResponseEntity.ok(body);
        } catch (Exception ex) {
            body.put("success", false);
            body.put("message", "Unable to send message. Please try again.");
            return ResponseEntity.status(500).body(body);
        }
    }
}
